package ua.orlov.betreactive.service;

import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.model.User;

import java.util.UUID;

public interface UserService {

    Mono<User> createUser(CreateUserRequest request);

    Mono<User> getUserById(UUID id);

}
