package ua.orlov.betreactive.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.mapper.BetMapper;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.repository.BetRepository;
import ua.orlov.betreactive.service.BetService;
import ua.orlov.betreactive.service.kafka.BetKafkaService;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor
public class BetServiceImpl implements BetService {

    private final BetRepository betRepository;
    private final BetMapper betMapper;
    private final BetKafkaService betKafkaService;

    @Override
    public Mono<Bet> createBet(CreateBetRequest request) {
        Bet mappedBet = betMapper.mapCreateBetRequestToBet(request);

        mappedBet.setId(UUID.randomUUID());
        mappedBet.setCreatedAt(LocalDateTime.now());

        return betRepository.save(mappedBet)
                .doOnSuccess(betKafkaService::sendEntity);
    }

    @Override
    public Mono<Bet> getBetById(UUID id) {
        return betRepository.findById(id);
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
}
