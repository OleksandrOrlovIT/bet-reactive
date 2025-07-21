package ua.orlov.betreactive.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.dto.UserCashInRequest;
import ua.orlov.betreactive.dto.UserCashOutRequest;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.service.UserService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(userController).build();
    }

    @Test
    void createUserThenSuccess() {
        CreateUserRequest request = new CreateUserRequest();
        User user = User.builder().id(UUID.randomUUID()).firstName("John").build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.getId().toString())
                .jsonPath("$.firstName").isEqualTo("John");

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    void getUserThenSuccess() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).firstName("John").build();

        when(userService.getUserById(any(UUID.class))).thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/api/v1/users/" + userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.getId().toString())
                .jsonPath("$.firstName").isEqualTo(user.getFirstName());

        verify(userService).getUserById(any(UUID.class));
    }

    @Test
    void getAllUsersThenSuccess() {
        User user1 = User.builder().id(UUID.randomUUID()).firstName("John1").build();
        User user2 = User.builder().id(UUID.randomUUID()).firstName("John2").build();

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(Flux.just(user1, user2));

        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(2)
                .contains(user1, user2);

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    void deleteUserThenSuccess() {
        UUID userId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/users/" + userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(userService).deleteUserById(any(UUID.class));
    }

    @Test
    void updateUserThenSuccess() {
        UpdateUserRequest request = new UpdateUserRequest();
        User user = User.builder().id(UUID.randomUUID()).firstName("John").build();

        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(Mono.just(user));

        webTestClient.put()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.getId().toString())
                .jsonPath("$.firstName").isEqualTo(user.getFirstName());

        verify(userService).updateUser(any(UpdateUserRequest.class));
    }

    @Test
    void cashInUserThenSuccess() {
        UserCashInRequest request = new UserCashInRequest();
        User user = User.builder().id(UUID.randomUUID()).firstName("John").build();

        when(userService.cashInToUserBalance(any(UserCashInRequest.class))).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/users/cash-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.getId().toString())
                .jsonPath("$.firstName").isEqualTo("John");

        verify(userService).cashInToUserBalance(any(UserCashInRequest.class));
    }

    @Test
    void cashOutUserThenSuccess() {
        UserCashOutRequest request = new UserCashOutRequest();
        User user = User.builder().id(UUID.randomUUID()).firstName("John").build();

        when(userService.cashOutToUserBalance(any(UserCashOutRequest.class))).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/users/cash-out")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.getId().toString())
                .jsonPath("$.firstName").isEqualTo("John");

        verify(userService).cashOutToUserBalance(any(UserCashOutRequest.class));
    }


}
