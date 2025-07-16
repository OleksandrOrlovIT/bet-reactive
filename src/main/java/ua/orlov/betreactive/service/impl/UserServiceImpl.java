package ua.orlov.betreactive.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.KafkaSender;
import ua.orlov.betreactive.configuration.KafkaProducerConfig;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.mapper.UserMapper;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.repository.UserRepository;
import ua.orlov.betreactive.service.UserService;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaSender<String, User> kafkaSender;

    @Override
    public Mono<User> createUser(CreateUserRequest request) {
        User mappedUser = userMapper.mapCreateUserRequestToUser(request);

        mappedUser.setId(UUID.randomUUID());

        return userRepository.save(mappedUser)
                .doOnSuccess(saved -> kafkaProducer.send(userSink, saved.toString()));
    }

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }
}
