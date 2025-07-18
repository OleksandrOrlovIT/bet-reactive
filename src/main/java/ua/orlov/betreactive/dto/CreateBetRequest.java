package ua.orlov.betreactive.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateBetRequest {

    private UUID userId;

    private BigDecimal amount;

    private BigDecimal coefficient;

    private String betType;

}
