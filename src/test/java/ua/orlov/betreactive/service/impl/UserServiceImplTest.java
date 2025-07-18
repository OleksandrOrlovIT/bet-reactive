package ua.orlov.betreactive.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.mapper.UserMapper;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.repository.UserRepository;
import ua.orlov.betreactive.service.kafka.UserKafkaService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserKafkaService userKafkaService;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void whenCreateUserThenSuccess() {
        User mappedUser = User.builder().id(UUID.randomUUID()).build();

        when(userMapper.mapCreateUserRequestToUser(any())).thenReturn(User.builder().build());
        when(userRepository.save(any())).thenReturn(Mono.just(mappedUser));

        StepVerifier.create(userService.createUser(new CreateUserRequest()))
                .expectNext(mappedUser)
                .verifyComplete();

        verify(userMapper, times(1)).mapCreateUserRequestToUser(any());
        verify(userRepository, times(1)).save(any());
        verify(userKafkaService, times(1)).sendEntity(any());
    }

    @Test
    void whenGetUserByIdThenSuccess() {
        User expectedUser = User.builder().build();
        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(userService.getUserById(UUID.randomUUID()))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void whenGetAllUsersThenSuccess() {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(user1, user2));

        StepVerifier.create(userService.getAllUsers(Pageable.unpaged()))
                .expectNext(user1, user2)
                .verifyComplete();

        verify(userRepository, times(1)).findAllBy(any(Pageable.class));
    }

    @Test
    void whenDeleteUserByIdThenSuccess() {
        when(userRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUserById(UUID.randomUUID()))
                .verifyComplete();

        verify(userRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenUpdateUserThenEntityNotFoundException() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(userId);

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());


        StepVerifier.create(userService.updateUser(request))
                .expectErrorMessage("User not found with id: " + userId)
                .verify();

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(0)).mapUpdateUserRequestToUser(any(UpdateUserRequest.class));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void whenUpdateUserThenSuccess() {
        UUID userId = UUID.randomUUID();
        User mappedUser = User.builder().id(userId).build();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(userId);

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(mappedUser));
        when(userMapper.mapUpdateUserRequestToUser(any(UpdateUserRequest.class))).thenReturn(mappedUser);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(mappedUser));

        StepVerifier.create(userService.updateUser(request))
                .expectNext(mappedUser)
                .verifyComplete();

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userMapper, times(1)).mapUpdateUserRequestToUser(any(UpdateUserRequest.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
