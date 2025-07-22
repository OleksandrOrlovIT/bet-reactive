package ua.orlov.betreactive.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.service.EventService;

import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/api/v1/events")
@RestController
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Mono<Event> createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/{id}")
    public Mono<Event> getEvent(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }

    @GetMapping
    public Flux<Event> getAllEvents(@RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return eventService.getAllEvents(PageRequest.of(pageNumber, pageSize));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteEvent(@PathVariable UUID id) {
        return eventService.deleteEventById(id);
    }

    @PutMapping
    public Mono<Event> updateEvent(@Valid @RequestBody UpdateEventRequest request) {
        return eventService.updateEvent(request);
    }

}
