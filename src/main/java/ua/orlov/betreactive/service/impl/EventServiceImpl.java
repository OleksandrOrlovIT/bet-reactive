package ua.orlov.betreactive.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.exceptions.EntityNotFoundException;
import ua.orlov.betreactive.mapper.EventMapper;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.repository.EventRepository;
import ua.orlov.betreactive.service.EventService;
import ua.orlov.betreactive.service.kafka.EventKafkaService;

import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventKafkaService eventKafkaService;

    @Override
    public Mono<Event> createEvent(CreateEventRequest request) {
        Event mappedEvent = eventMapper.mapCreateEventRequestToEvent(request);

        mappedEvent.setId(UUID.randomUUID());

        return eventRepository.save(mappedEvent)
                .doOnSuccess(eventKafkaService::sendEntity);
    }

    @Override
    public Mono<Event> getEventById(UUID id) {
        return eventRepository.findById(id);
    }

    @Override
    public Flux<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAllBy(pageable);
    }

    @Override
    public Mono<Void> deleteEventById(UUID id) {
        return eventRepository.deleteById(id)
                .doOnTerminate(() -> log.info("Deleted event from DB: {}", id));
    }

    @Override
    public Mono<Event> updateEvent(UpdateEventRequest request) {
        return getEventById(request.getId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Event not found with id: " + request.getId())))
                .flatMap(existingEvent -> {
                    Event updatedEvent = eventMapper.mapUpdateEventRequestToEvent(request);
                    return eventRepository.save(updatedEvent);
                });
    }
}
