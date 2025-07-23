package ua.orlov.betreactive.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    private final ReactiveMongoTemplate mongoTemplate;

    @Value("${kafka.topic.general}")
    private String generalTopic;

    private static final String FINANCIAL_SUCCESS_MESSAGE = "%s successful for user: %s, old balance: %s, new balance: %s";
    private static final String ERROR_MESSAGE = "%s failed for userId: %s, reason: %s";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: %s";

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
    public Mono<User> cashInToUserBalance(UserCashInRequest request) {
        return updateBalanceAtomically(request.getUserId(), request.getAmount(), "Cash in", true);
    }

    @Override
    public Mono<User> cashOutToUserBalance(UserCashOutRequest request) {
        return updateBalanceAtomically(request.getUserId(), request.getAmount(), "Cash out", false);
    }

    private Mono<User> updateBalanceAtomically(UUID userId, BigDecimal amount, String action, boolean isCashIn) {
        if (amount == null || amount.signum() <= 0) {
            return Mono.error(new IllegalArgumentException("Amount must be greater than zero"));
        }

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId))))
                .flatMap(user -> {
                    BigDecimal oldBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

                    if (!isCashIn && oldBalance.compareTo(amount) < 0) {
                        return Mono.error(new IllegalArgumentException("Insufficient funds"));
                    }

                    Update update = new Update();
                    update.inc("balance", isCashIn ? amount : amount.negate());

                    Query query = new Query(Criteria.where("id").is(userId));

                    return mongoTemplate.findAndModify(query, update,
                                    FindAndModifyOptions.options().returnNew(true), User.class)
                            .flatMap(updatedUser -> {
                                return generalKafkaService.send(generalTopic, updatedUser.getId().toString(),
                                                String.format(FINANCIAL_SUCCESS_MESSAGE, action, updatedUser.getId(), oldBalance, updatedUser.getBalance()))
                                        .thenReturn(updatedUser);
                            });
                })
                .onErrorResume(error -> generalKafkaService.send(generalTopic, error.getMessage(),
                                String.format(ERROR_MESSAGE, action, userId, error.getMessage()))
                        .then(Mono.error(error)));
    }
}
