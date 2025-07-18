package ua.orlov.betreactive.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.service.EventService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(eventController).build();
    }

    @Test
    void createEventThenSuccess() {
        CreateEventRequest request = new CreateEventRequest();
        Event event = Event.builder().id(UUID.randomUUID()).name("Event Name").build();

        when(eventService.createEvent(any(CreateEventRequest.class))).thenReturn(Mono.just(event));

        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(event.getId().toString())
                .jsonPath("$.name").isEqualTo(event.getName());

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }

    @Test
    void getEventThenSuccess() {
        UUID eventId = UUID.randomUUID();
        Event event = Event.builder().id(UUID.randomUUID()).name("Event Name").build();

        when(eventService.getEventById(any(UUID.class))).thenReturn(Mono.just(event));

        webTestClient.get()
                .uri("/api/v1/events/" + eventId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(event.getId().toString())
                .jsonPath("$.name").isEqualTo(event.getName());

        verify(eventService).getEventById(any(UUID.class));
    }

    @Test
    void getAllEventsThenSuccess() {
        Event event1 = Event.builder().id(UUID.randomUUID()).name("Event Name1").build();
        Event event2 = Event.builder().id(UUID.randomUUID()).name("Event Name2").build();

        when(eventService.getAllEvents(any(Pageable.class))).thenReturn(Flux.just(event1, event2));

        webTestClient.get()
                .uri("/api/v1/events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(2)
                .contains(event1, event2);

        verify(eventService).getAllEvents(any(Pageable.class));
    }

    @Test
    void deleteEventThenSuccess() {
        UUID eventId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/events/" + eventId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(eventService).deleteEventById(any(UUID.class));
    }

    @Test
    void updateEventThenSuccess() {
        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        Event event = Event.builder().id(UUID.randomUUID()).name("Event Name").build();

        when(eventService.updateEvent(any(UpdateEventRequest.class))).thenReturn(Mono.just(event));

        webTestClient.put()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateEventRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(event.getId().toString())
                .jsonPath("$.name").isEqualTo(event.getName());

        verify(eventService).updateEvent(any(UpdateEventRequest.class));
    }

}
