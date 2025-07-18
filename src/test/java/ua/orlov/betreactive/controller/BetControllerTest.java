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
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.model.BetType;
import ua.orlov.betreactive.service.BetService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BetControllerTest {

    @Mock
    private BetService betService;

    @InjectMocks
    private BetController betController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(betController).build();
    }

    @Test
    void createBetThenSuccess() {
        CreateBetRequest request = new CreateBetRequest();
        Bet bet = Bet.builder().id(UUID.randomUUID()).betType(BetType.WIN).build();

        when(betController.createBet(any(CreateBetRequest.class))).thenReturn(Mono.just(bet));

        webTestClient.post()
                .uri("/api/v1/bets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(bet.getId().toString())
                .jsonPath("$.betType").isEqualTo(bet.getBetType().name());

        verify(betService).createBet(any(CreateBetRequest.class));
    }

    @Test
    void getBetThenSuccess() {
        UUID betId = UUID.randomUUID();
        Bet bet = Bet.builder().id(UUID.randomUUID()).betType(BetType.WIN).build();

        when(betService.getBetById(any(UUID.class))).thenReturn(Mono.just(bet));

        webTestClient.get()
                .uri("/api/v1/bets/" + betId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(bet.getId().toString())
                .jsonPath("$.betType").isEqualTo(bet.getBetType().name());

        verify(betService).getBetById(any(UUID.class));
    }

    @Test
    void getAllBetsThenSuccess() {
        Bet bet1 = Bet.builder().id(UUID.randomUUID()).betType(BetType.WIN).build();
        Bet bet2 = Bet.builder().id(UUID.randomUUID()).betType(BetType.LOSE).build();

        when(betService.getAllBets(any(Pageable.class))).thenReturn(Flux.just(bet1, bet2));

        webTestClient.get()
                .uri("/api/v1/bets")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Bet.class)
                .hasSize(2)
                .contains(bet1, bet2);

        verify(betService).getAllBets(any(Pageable.class));
    }

    @Test
    void deleteBetThenSuccess() {
        UUID betId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/bets/" + betId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(betService).deleteBetById(any(UUID.class));
    }

}
