package ua.orlov.betreactive.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.model.EventStatus;
import ua.orlov.betreactive.service.kafka.GeneralKafkaService;

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
                                    return Flux.fromIterable(bets)
                                            .flatMap(betService::computeWonBet)
                                            .then(generalKafkaService.sendToGeneral(event.getId().toString(),
                                                    String.format("Processed %d bets for expired event: %s", betCount, event.getName())))
                                            .then(Mono.defer(() -> {
                                                event.setStatus(EventStatus.EXPIRED);
                                                return eventService.updateEvent(event);
                                            }));
                                })
                                .flatMap(updatedEvent -> {
                                    String successMessage = String.format("Event '%s' marked as EXPIRED", updatedEvent.getName());
                                    return generalKafkaService.sendToGeneral(updatedEvent.getId().toString(), successMessage)
                                            .thenReturn(updatedEvent);
                                })
                                .doOnError(error -> {
                                    String errorMessage = String.format("Failed processing event '%s': %s", event.getName(), error.getMessage());
                                    generalKafkaService.sendToGeneral(event.getId().toString(), errorMessage).subscribe();
                                })
                )
                .onErrorContinue((throwable, obj) -> {
                    String errorMessage = String.format("Error processing event: %s", throwable.getMessage());
                    generalKafkaService.sendToGeneral("system", errorMessage).subscribe();
                })
                .subscribe();
    }
}
