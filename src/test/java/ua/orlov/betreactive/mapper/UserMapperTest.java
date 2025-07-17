package ua.orlov.betreactive.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void mapCreateUserRequestToUserThenSuccess() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFirstName("John");
        createUserRequest.setLastName("Doe");
        createUserRequest.setEmail("johndoe@gmail.com");

        User user = userMapper.mapCreateUserRequestToUser(createUserRequest);

        assertEquals(createUserRequest.getFirstName(), user.getFirstName());
        assertEquals(createUserRequest.getLastName(), user.getLastName());
        assertEquals(createUserRequest.getEmail(), user.getEmail());
    }

    @Test
    void mapUpdateUserRequestToUserThenSuccess() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(UUID.randomUUID());
        updateUserRequest.setFirstName("John");
        updateUserRequest.setLastName("Doe");
        updateUserRequest.setEmail("johndoe@gmail.com");

        User user = userMapper.mapUpdateUserRequestToUser(updateUserRequest);

        assertEquals(updateUserRequest.getId(), user.getId());
        assertEquals(updateUserRequest.getFirstName(), user.getFirstName());
        assertEquals(updateUserRequest.getLastName(), user.getLastName());
        assertEquals(updateUserRequest.getEmail(), user.getEmail());
    }
}