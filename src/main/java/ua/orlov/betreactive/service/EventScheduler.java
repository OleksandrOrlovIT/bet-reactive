package ua.orlov.betreactive.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.model.EventStatus;
import ua.orlov.betreactive.service.kafka.GeneralKafkaService;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;
    private final BetService betService;
    private final GeneralKafkaService generalKafkaService;

    @Scheduled(fixedRateString = "${scheduler.rate.event-ms}")
    public void checkExpiredEvents() {
        eventService.getAllEndedEvents()
                .flatMap(event ->
                        betService.getAllBetsByEventId(event.getId())
                                .collectList()
                                .flatMap(bets -> {
                                    int betCount = bets.size();

                                    if (betCount == 0) {
                                        event.setStatus(EventStatus.EXPIRED);
                                        return eventService.updateEvent(event)
                                                .flatMap(updatedEvent -> generalKafkaService
                                                        .sendToGeneral(updatedEvent.getId().toString(),
                                                                String.format("Event '%s' had no bets. Marked as EXPIRED.", updatedEvent.getName()))
                                                        .thenReturn(updatedEvent));
                                    }

                                    event.setStatus(EventStatus.EXPIRED);
                                    return Flux.fromIterable(bets)
                                            .flatMap(betService::computeWonBet)
                                            .then(generalKafkaService.sendToGeneral(event.getId().toString(),
                                                    String.format("Processed %d bets for expired event: %s", betCount, event.getName())))
                                            .then(eventService.updateEvent(event));
                                })
                                .flatMap(updatedEvent -> generalKafkaService
                                        .sendToGeneral(updatedEvent.getId().toString(),
                                                String.format("Event '%s' marked as EXPIRED", updatedEvent.getName()))
                                        .thenReturn(updatedEvent))
                                .doOnError(error -> generalKafkaService
                                        .sendToGeneral(event.getId().toString(),
                                                String.format("Failed processing event '%s': %s", event.getName(), error.getMessage()))
                                        .subscribe())
                )
                .onErrorContinue((throwable, obj) -> generalKafkaService
                        .sendToGeneral("system", String.format("Error processing event: %s", throwable.getMessage()))
                        .subscribe())
                .subscribe();
    }

}
