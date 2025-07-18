package ua.orlov.betreactive.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ua.orlov.betreactive.model.Event;

import java.util.UUID;

@Repository
public interface EventRepository extends ReactiveMongoRepository<Event, UUID> {

    Flux<Event> findAllBy(Pageable pageable);

}
