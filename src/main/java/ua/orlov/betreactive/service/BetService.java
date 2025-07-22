package ua.orlov.betreactive.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.model.Bet;

import java.util.UUID;

public interface BetService {

    Mono<Bet> createBet(CreateBetRequest request);

    Mono<Bet> getBetById(UUID id);

    Flux<Bet> getAllBets(Pageable pageable);

    Mono<Void> deleteBetById(UUID id);

    Flux<Bet> getAllBetsByEventId(UUID eventId);

    Mono<Void> computeWonBet(Bet bet);
}
