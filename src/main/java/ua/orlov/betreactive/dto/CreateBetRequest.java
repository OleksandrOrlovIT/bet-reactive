package ua.orlov.betreactive.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateBetRequest {

    @NotNull(message = "userId is required")
    private UUID userId;

    @NotNull(message = "eventId is required")
    private UUID eventId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1.0")
    private BigDecimal amount;

    @NotNull(message = "coefficient is required")
    @DecimalMin(value = "1.0", inclusive = false)
    private BigDecimal coefficient;

    @NotBlank(message = "betType is required")
    private String betType;

}
