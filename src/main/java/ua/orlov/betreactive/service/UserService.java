package ua.orlov.betreactive.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.model.User;

import java.util.UUID;

public interface UserService {

    Mono<User> createUser(CreateUserRequest request);

    Mono<User> getUserById(UUID id);

    Flux<User> getAllUsers(Pageable pageable);

    Mono<Void> deleteUserById(UUID id);

    Mono<User> updateUser(UpdateUserRequest request);

}
