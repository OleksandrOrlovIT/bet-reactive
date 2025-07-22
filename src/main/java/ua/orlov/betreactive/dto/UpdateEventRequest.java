package ua.orlov.betreactive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateEventRequest {

    @NotNull(message = "id is required")
    private UUID id;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "startDate is required")
    private LocalDateTime startDate;

    @NotNull(message = "endDate is required")
    private LocalDateTime endDate;

}
