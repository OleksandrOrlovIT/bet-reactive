package ua.orlov.betreactive.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.mapper.UserMapper;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.repository.UserRepository;
import ua.orlov.betreactive.service.UserService;
import ua.orlov.betreactive.service.kafka.UserKafkaService;

import java.util.UUID;

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
}
