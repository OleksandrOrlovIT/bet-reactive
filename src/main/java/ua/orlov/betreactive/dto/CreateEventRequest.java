package ua.orlov.betreactive.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}
