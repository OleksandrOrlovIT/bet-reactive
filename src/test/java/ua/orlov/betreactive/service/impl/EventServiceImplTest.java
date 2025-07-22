package ua.orlov.betreactive.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.mapper.EventMapper;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.repository.EventRepository;
import ua.orlov.betreactive.service.kafka.EventKafkaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventKafkaService eventKafkaService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void whenCreateEventThenSuccess() {
        CreateEventRequest request = new CreateEventRequest();
        request.setName("name");
        request.setStartDate(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
        request.setEndDate(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.now()));

        Event mappedEvent = Event.builder().build();

        when(eventMapper.mapCreateEventRequestToEvent(any())).thenReturn(mappedEvent);
        when(eventRepository.save(any())).thenReturn(Mono.just(mappedEvent));

        StepVerifier.create(eventService.createEvent(request))
                .expectNext(mappedEvent)
                .verifyComplete();

        verify(eventMapper, times(1)).mapCreateEventRequestToEvent(any());
        verify(eventRepository, times(1)).save(any());
        verify(eventKafkaService, times(1)).sendEntity(any());
    }

    @Test
    void whenCreateEventThenStartDateAfterEndDateThenException() {
        CreateEventRequest request = new CreateEventRequest();
        request.setName("name");
        request.setStartDate(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
        request.setEndDate(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.now()));

        StepVerifier.create(eventService.createEvent(request))
                .expectErrorMessage("Start date should be after end date of the event")
                .verify();

        verify(eventMapper, times(0)).mapCreateEventRequestToEvent(any());
        verify(eventRepository, times(0)).save(any());
        verify(eventKafkaService, times(0)).sendEntity(any());
    }

    @Test
    void whenGetEventByIdThenSuccess() {
        Event expectedEvent = Event.builder().build();
        when(eventRepository.findById(any(UUID.class))).thenReturn(Mono.just(expectedEvent));

        StepVerifier.create(eventService.getEventById(UUID.randomUUID()))
                .expectNext(expectedEvent)
                .verifyComplete();

        verify(eventRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void whenGetAllEventsThenSuccess() {
        Event event1 = Event.builder().build();
        Event event2 = Event.builder().build();
        when(eventRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(event1, event2));

        StepVerifier.create(eventService.getAllEvents(Pageable.unpaged()))
                .expectNext(event1, event2)
                .verifyComplete();

        verify(eventRepository, times(1)).findAllBy(any(Pageable.class));
    }

    @Test
    void whenDeleteEventByIdThenSuccess() {
        when(eventRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(eventService.deleteEventById(UUID.randomUUID()))
                .verifyComplete();

        verify(eventRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenUpdateEventThenEntityNotFoundException() {
        UUID eventId = UUID.randomUUID();
        UpdateEventRequest request = new UpdateEventRequest();
        request.setId(eventId);

        when(eventRepository.findById(any(UUID.class))).thenReturn(Mono.empty());


        StepVerifier.create(eventService.updateEvent(request))
                .expectErrorMessage("Event not found with id: " + eventId)
                .verify();

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventMapper, times(0)).mapUpdateEventRequestToEvent(any(UpdateEventRequest.class));
        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    void whenUpdateEventThenSuccess() {
        UUID eventId = UUID.randomUUID();
        Event mappedEvent = Event.builder().id(eventId).build();
        UpdateEventRequest request = new UpdateEventRequest();
        request.setId(eventId);

        when(eventRepository.findById(any(UUID.class))).thenReturn(Mono.just(mappedEvent));
        when(eventMapper.mapUpdateEventRequestToEvent(any(UpdateEventRequest.class))).thenReturn(mappedEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(mappedEvent));

        StepVerifier.create(eventService.updateEvent(request))
                .expectNext(mappedEvent)
                .verifyComplete();

        verify(eventRepository, times(1)).findById(any(UUID.class));
        verify(eventMapper, times(1)).mapUpdateEventRequestToEvent(any(UpdateEventRequest.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

}
