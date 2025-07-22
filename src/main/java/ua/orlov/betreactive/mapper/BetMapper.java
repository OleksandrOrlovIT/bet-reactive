package ua.orlov.betreactive.mapper;

import org.springframework.stereotype.Component;
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.model.BetType;

@Component
public class BetMapper {

    public Bet mapCreateBetRequestToBet(CreateBetRequest createBetRequest) {
        return Bet.builder()
                .userId(createBetRequest.getUserId())
                .eventId(createBetRequest.getEventId())
                .amount(createBetRequest.getAmount())
                .coefficient(createBetRequest.getCoefficient())
                .betType(BetType.fromStringIgnoreCase(createBetRequest.getBetType()))
                .build();
    }

}
