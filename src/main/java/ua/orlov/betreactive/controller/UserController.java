package ua.orlov.betreactive.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.service.UserService;

import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public Mono<User> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/user/{id}")
    public Mono<User> getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

}
