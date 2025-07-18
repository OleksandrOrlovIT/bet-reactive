package ua.orlov.betreactive.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.model.BetType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BetMapperTest {

    @InjectMocks
    private BetMapper betMapper;

    @Test
    void mapCreateBetRequestToBetThenSuccess() {
        CreateBetRequest createBetRequest = new CreateBetRequest();
        createBetRequest.setUserId(UUID.randomUUID());
        createBetRequest.setBetType("Win");
        createBetRequest.setAmount(BigDecimal.ONE);
        createBetRequest.setCoefficient(BigDecimal.ONE);

        Bet bet = betMapper.mapCreateBetRequestToBet(createBetRequest);

        assertAll(
                () -> assertEquals(createBetRequest.getUserId(), bet.getUserId()),
                () -> assertEquals(BetType.fromStringIgnoreCase(createBetRequest.getBetType()), bet.getBetType()),
                () -> assertEquals(createBetRequest.getAmount(), bet.getAmount()),
                () -> assertEquals(createBetRequest.getCoefficient(), bet.getCoefficient())
        );
    }
}
