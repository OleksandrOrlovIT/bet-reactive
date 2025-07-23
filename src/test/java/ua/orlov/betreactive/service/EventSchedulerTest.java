package ua.orlov.betreactive.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.service.kafka.GeneralKafkaService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSchedulerTest {

    @Mock
    private EventService eventService;

    @Mock
    private BetService betService;

    @Mock
    private GeneralKafkaService generalKafkaService;

    @InjectMocks
    private EventScheduler eventScheduler;

    @Test
    void checkExpiredEventsWhenGetAllEndedEventsThenException() {
        when(eventService.getAllEndedEvents()).thenReturn(Flux.error(new RuntimeException("Something went wrong")));
        when(generalKafkaService.sendToGeneral(eq("system"), contains("Something went wrong")))
                .thenReturn(Mono.empty());

        eventScheduler.checkExpiredEvents();

        verify(eventService, times(1)).getAllEndedEvents();
        verify(generalKafkaService).sendToGeneral(eq("system"), contains("Something went wrong"));
    }

    @Test
    void checkExpiredEventsWhenGetAllBetsThenException() {
        Event event = Event.builder().id(UUID.randomUUID()).name("name").build();
        when(eventService.getAllEndedEvents()).thenReturn(Flux.just(event));
        when(betService.getAllBetsByEventId(any(UUID.class))).thenReturn(Flux.error(new RuntimeException("Something went wrong")));
        when(generalKafkaService.sendToGeneral(eq(event.getId().toString()), contains("Something went wrong")))
                .thenReturn(Mono.empty());

        eventScheduler.checkExpiredEvents();

        verify(eventService, times(1)).getAllEndedEvents();
        verify(betService, times(1)).getAllBetsByEventId(any(UUID.class));
        verify(generalKafkaService).sendToGeneral(eq(event.getId().toString()), contains("Something went wrong"));
    }

    @Test
    void checkExpiredEventsWhen0BetsThenException() {
        Event event = Event.builder().id(UUID.randomUUID()).name("name").build();
        when(eventService.getAllEndedEvents()).thenReturn(Flux.just(event));
        when(betService.getAllBetsByEventId(any(UUID.class))).thenReturn(Flux.empty());
        when(eventService.updateEvent(any(Event.class))).thenReturn(Mono.just(event));
        when(generalKafkaService.sendToGeneral(
                eq(event.getId().toString()),
                contains("Event '" + event.getName() + "' had no bets. Marked as EXPIRED.")))
                .thenReturn(Mono.empty());

        eventScheduler.checkExpiredEvents();

        verify(eventService, times(1)).getAllEndedEvents();
        verify(eventService, times(1)).updateEvent(any(Event.class));
        verify(generalKafkaService).sendToGeneral(
                eq(event.getId().toString()),
                contains("Event '" + event.getName() + "' had no bets. Marked as EXPIRED."));
    }

    @Test
    void checkExpiredEventsWhen2BetsThenSuccess() {
        Event event = Event.builder().id(UUID.randomUUID()).name("name").build();
        Bet bet1 = new Bet();
        Bet bet2 = new Bet();

        when(eventService.getAllEndedEvents()).thenReturn(Flux.just(event));
        when(betService.getAllBetsByEventId(any(UUID.class))).thenReturn(Flux.just(bet1, bet2));
        when(betService.computeWonBet(any(Bet.class))).thenReturn(Mono.empty());
        when(eventService.updateEvent(any(Event.class))).thenReturn(Mono.just(event));

        when(generalKafkaService.sendToGeneral(
                eq(event.getId().toString()),
                contains(String.format("Processed %d bets for expired event: %s", 2, event.getName()))))
                .thenReturn(Mono.empty());

        when(generalKafkaService.sendToGeneral(
                eq(event.getId().toString()),
                contains(String.format("Event '%s' marked as EXPIRED", event.getName()))))
                .thenReturn(Mono.empty());

        eventScheduler.checkExpiredEvents();

        verify(eventService, times(1)).getAllEndedEvents();
        verify(eventService, times(1)).updateEvent(any(Event.class));
        verify(betService, times(2)).computeWonBet(any(Bet.class));
        verify(generalKafkaService).sendToGeneral(
                eq(event.getId().toString()),
                contains(String.format("Processed %d bets for expired event: %s", 2, event.getName())));
        verify(generalKafkaService).sendToGeneral(
                eq(event.getId().toString()),
                contains(String.format("Event '%s' marked as EXPIRED", event.getName())));
    }
}
