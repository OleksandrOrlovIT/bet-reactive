package ua.orlov.betreactive.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserCashOutRequest {

    @NotNull(message = "userId is required")
    private UUID userId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1.0")
    private BigDecimal amount;

}
