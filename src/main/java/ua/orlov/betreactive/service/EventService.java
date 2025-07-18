package ua.orlov.betreactive.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.model.Event;

import java.util.UUID;

public interface EventService {

    Mono<Event> createEvent(CreateEventRequest request);

    Mono<Event> getEventById(UUID id);

    Flux<Event> getAllEvents(Pageable pageable);

    Mono<Void> deleteEventById(UUID id);

    Mono<Event> updateEvent(UpdateEventRequest request);

}
