package ua.orlov.betreactive.service.impl;

import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.dto.UserCashInRequest;
import ua.orlov.betreactive.dto.UserCashOutRequest;
import ua.orlov.betreactive.exceptions.EntityNotFoundException;
import ua.orlov.betreactive.mapper.UserMapper;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.repository.UserRepository;
import ua.orlov.betreactive.service.UserService;
import ua.orlov.betreactive.service.kafka.GeneralKafkaService;
import ua.orlov.betreactive.service.kafka.UserKafkaService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserKafkaService userKafkaService;
    private final GeneralKafkaService generalKafkaService;
    private final TransactionalOperator transactionalOperator;

    @Value("${kafka.topic.general}")
    private String generalTopic;

    private static final String FINANCIAL_SUCCESS_MESSAGE = "%s successful for user: %s, old balance: %s, new balance: %s";
    private static final String ERROR_MESSAGE = "%s failed for userId: %s, reason: %s";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: %s";
    private static final String AMOUNT_LESS_THAN_ZERO_MESSAGE = "Amount can't be null must be greater than zero";
    private static final String CASH_OUT_INSUFFICIENT_FUNDS_MESSAGE = "Insufficient balance for cash out";

    @Override
    public Mono<User> createUser(CreateUserRequest request) {
        User mappedUser = userMapper.mapCreateUserRequestToUser(request);

        mappedUser.setId(UUID.randomUUID());

        mappedUser.setBalance(BigDecimal.ZERO);

        return userRepository.save(mappedUser)
                .doOnSuccess(userKafkaService::sendEntity);
    }

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, id))));
    }

    @Override
    public Flux<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllBy(pageable);
    }

    @Override
    public Mono<Void> deleteUserById(UUID id) {
        return userRepository.deleteById(id)
                .doOnTerminate(() -> log.info("Deleted user from DB: {}", id));
    }

    @Override
    public Mono<User> updateUser(UpdateUserRequest request) {
        return getUserById(request.getId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, request.getId()))))
                .flatMap(existingUser -> {
                    User updatedUser = userMapper.mapUpdateUserRequestToUser(request);
                    return userRepository.save(updatedUser);
                });
    }

    @Override
    @Transactional
    public Mono<User> cashInToUserBalance(UserCashInRequest request) {
        return validateUserAndAmount(request.getUserId(), request.getAmount())
                .flatMap(user -> {
                    BigDecimal old = oldBalance(user);
                    BigDecimal newBalance = old.add(request.getAmount());
                    return updateUserBalance(user, newBalance, "Cash in", old);
                })
                .as(transactionalOperator::transactional)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isTransientDatabaseError)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new IllegalStateException("Transaction failed after retries", retrySignal.failure()))
                );
    }

    private boolean isTransientDatabaseError(Throwable throwable) {
        if (throwable instanceof OptimisticLockingFailureException) {
            return true;
        }
        if (throwable instanceof MongoException && ((MongoException) throwable).getCode() == 251) {
            return true;
        }
        return false;
    }

    @Override
    public Mono<User> cashOutToUserBalance(UserCashOutRequest request) {
        return validateUserAmountAndBalance(request.getUserId(), request.getAmount())
                .flatMap(user -> {
                    BigDecimal old = oldBalance(user);
                    BigDecimal newBalance = old.subtract(request.getAmount());
                    return updateUserBalance(user, newBalance, "Cash out", old);
                });
    }

    private BigDecimal oldBalance(User user) {
        return user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
    }

    private Mono<User> validateUserAndAmount(UUID userId, BigDecimal amount) {
        return getUserById(userId)
                .filter(user -> amount != null && amount.signum() > 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(AMOUNT_LESS_THAN_ZERO_MESSAGE)));
    }

    private Mono<User> validateUserAmountAndBalance(UUID userId, BigDecimal amount) {
        return validateUserAndAmount(userId, amount)
                .filter(user -> oldBalance(user).compareTo(amount) >= 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(CASH_OUT_INSUFFICIENT_FUNDS_MESSAGE)));
    }

    private Mono<User> updateUserBalance(User user, BigDecimal newBalance, String action, BigDecimal oldBalance) {
        user.setBalance(newBalance);
        return userRepository.save(user)
                .flatMap(updatedUser -> generalKafkaService.send(generalTopic, updatedUser.getId().toString(),
                                String.format(FINANCIAL_SUCCESS_MESSAGE, action, updatedUser.getId(), oldBalance, updatedUser.getBalance()))
                        .thenReturn(updatedUser))
                .onErrorResume(error -> generalKafkaService.send(generalTopic, error.getMessage(),
                                String.format(ERROR_MESSAGE, action, user.getId(), error.getMessage()))
                        .then(Mono.error(error)));
    }
}
