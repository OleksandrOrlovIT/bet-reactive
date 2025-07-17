package ua.orlov.betreactive.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserRequest {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;


}
