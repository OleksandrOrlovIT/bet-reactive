package ua.orlov.betreactive.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.exceptions.EntityNotFoundException;
import ua.orlov.betreactive.mapper.UserMapper;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.repository.UserRepository;
import ua.orlov.betreactive.service.UserService;
import ua.orlov.betreactive.service.kafka.UserKafkaService;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserKafkaService userKafkaService;

    @Override
    public Mono<User> createUser(CreateUserRequest request) {
        User mappedUser = userMapper.mapCreateUserRequestToUser(request);

        mappedUser.setId(UUID.randomUUID());

        return userRepository.save(mappedUser)
                .doOnSuccess(userKafkaService::sendUser);
    }

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
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
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + request.getId())))
                .flatMap(existingUser -> {
                    User updatedUser = userMapper.mapUpdateUserRequestToUser(request);
                    return userRepository.save(updatedUser);
                });
    }
}
