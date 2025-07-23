package ua.orlov.betreactive.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.dto.UserCashInRequest;
import ua.orlov.betreactive.exceptions.EntityNotFoundException;
import ua.orlov.betreactive.mapper.BetMapper;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.model.User;
import ua.orlov.betreactive.repository.BetRepository;
import ua.orlov.betreactive.service.BetService;
import ua.orlov.betreactive.service.EventService;
import ua.orlov.betreactive.service.UserService;
import ua.orlov.betreactive.service.kafka.BetKafkaService;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor
public class BetServiceImpl implements BetService {

    private final BetRepository betRepository;
    private final UserService userService;
    private final EventService eventService;
    private final BetMapper betMapper;
    private final BetKafkaService betKafkaService;

    private static final String BET_NOT_FOUND_MESSAGE = "Bet not found with id: %s";

    @Override
    public Mono<Bet> createBet(CreateBetRequest request) {
        Bet mappedBet = betMapper.mapCreateBetRequestToBet(request);
        mappedBet.setId(UUID.randomUUID());
        mappedBet.setCreatedAt(LocalDateTime.now());

        Mono<User> userMono = userService.getUserById(mappedBet.getUserId());
        Mono<Event> eventMono = eventService.getEventById(mappedBet.getEventId());

        return userMono.zipWith(eventMono)
                .flatMap(tuple -> betRepository.save(mappedBet))
                .doOnSuccess(betKafkaService::sendEntity);
    }

    @Override
    public Mono<Bet> getBetById(UUID id) {
        return betRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(BET_NOT_FOUND_MESSAGE, id))));
    }

    @Override
    public Flux<Bet> getAllBets(Pageable pageable) {
        return betRepository.findAllBy(pageable);
    }

    @Override
    public Mono<Void> deleteBetById(UUID id) {
        return betRepository.deleteById(id)
                .doOnTerminate(() -> log.info("Deleted bet from DB: {}", id));
    }

    @Override
    public Flux<Bet> getAllBetsByEventId(UUID eventId) {
        return betRepository.findAllByEventId(eventId);
    }

    @Override
    public Mono<Void> computeWonBet(Bet bet) {
        return userService.cashInToUserBalance(new UserCashInRequest(bet.getUserId(), bet.getAmount().multiply(bet.getCoefficient())))
                .then();
    }
}
