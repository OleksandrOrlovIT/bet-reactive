package ua.orlov.betreactive.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "bets")
public class Bet extends BaseEntity{

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "amount")
    private BigDecimal amount;

    @Field(name = "coefficient")
    private BigDecimal coefficient;

    @Field(name = "bet_type")
    private BetType betType;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

}
