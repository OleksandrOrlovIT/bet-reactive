package ua.orlov.betreactive.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.service.BetService;

import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/api/v1/bets")
@RestController
public class BetController {

    private final BetService betService;

    @PostMapping
    public Mono<Bet> createBet(@Valid @RequestBody CreateBetRequest request) {
        return betService.createBet(request);
    }

    @GetMapping("/{id}")
    public Mono<Bet> getBet(@PathVariable UUID id) {
        return betService.getBetById(id);
    }

    @GetMapping
    public Flux<Bet> getAllBets(@RequestParam(defaultValue = "0") int pageNumber,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return betService.getAllBets(PageRequest.of(pageNumber, pageSize));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBet(@PathVariable UUID id) {
        return betService.deleteBetById(id);
    }

    @GetMapping("/event-id")
    public Flux<Bet> getBetsByEventId(@RequestParam UUID eventId) {
        return betService.getAllBetsByEventId(eventId);
    }

}
