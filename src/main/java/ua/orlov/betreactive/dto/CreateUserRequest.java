package ua.orlov.betreactive.dto;

import lombok.Data;

@Data
public class CreateUserRequest {

    private String firstName;

    private String lastName;

    private String email;

}
