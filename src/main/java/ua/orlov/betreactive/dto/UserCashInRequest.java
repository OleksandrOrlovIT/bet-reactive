package ua.orlov.betreactive.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserCashInRequest {

    private UUID userId;

    private BigDecimal amount;
}
