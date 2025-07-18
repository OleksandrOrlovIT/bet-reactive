package ua.orlov.betreactive.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ua.orlov.betreactive.model.Bet;

import java.util.UUID;

@Repository
public interface BetRepository extends ReactiveMongoRepository<Bet, UUID> {

    Flux<Bet> findAllBy(Pageable pageable);

}
