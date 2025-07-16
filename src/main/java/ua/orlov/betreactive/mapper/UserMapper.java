package ua.orlov.betreactive.mapper;

import org.springframework.stereotype.Component;
import ua.orlov.betreactive.dto.CreateUserRequest;
import ua.orlov.betreactive.model.User;

@Component
public class UserMapper {

    public User mapCreateUserRequestToUser(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        return user;
    }

}
