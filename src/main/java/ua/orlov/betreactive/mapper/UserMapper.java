package ua.orlov.betreactive.mapper;

import org.springframework.stereotype.Component;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.dto.UpdateUserRequest;
import ua.orlov.betreactive.model.User;

@Component
public class UserMapper {

    public User mapCreateUserRequestToUser(CreateUserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();
    }

    public User mapUpdateUserRequestToUser(UpdateUserRequest request) {
        return User.builder()
                .id(request.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();
    }

}
