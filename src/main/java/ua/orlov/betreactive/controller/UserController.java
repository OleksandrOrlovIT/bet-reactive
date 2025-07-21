package ua.orlov.betreactive.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.UserCashInRequest;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.dto.UserCashOutRequest;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.service.UserService;

import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<User> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Flux<User> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return userService.getAllUsers(PageRequest.of(pageNumber, pageSize));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUserById(id);
    }

    @PutMapping
    public Mono<User> updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @PostMapping("/cash-in")
    public Mono<User> cashIn(@RequestBody UserCashInRequest request) {
        return userService.cashInToUserBalance(request);
    }

    @PostMapping("/cash-out")
    public Mono<User> cashOut(@RequestBody UserCashOutRequest request) {
        return userService.cashOutToUserBalance(request);
    }
}
