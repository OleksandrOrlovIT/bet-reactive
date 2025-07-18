package ua.orlov.betreactive.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateEventRequest {

    private UUID id;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}
