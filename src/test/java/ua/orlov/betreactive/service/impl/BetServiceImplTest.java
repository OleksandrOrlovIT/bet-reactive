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
import ua.orlov.betreactive.dto.CreateBetRequest;
import ua.orlov.betreactive.mapper.BetMapper;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.repository.BetRepository;
import ua.orlov.betreactive.service.kafka.BetKafkaService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BetServiceImplTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private BetMapper betMapper;

    @Mock
    private BetKafkaService betKafkaService;

    @InjectMocks
    private BetServiceImpl betService;

    @Test
    void whenCreateBetThenSuccess() {
        Bet mappedBet = Bet.builder().id(UUID.randomUUID()).build();

        when(betMapper.mapCreateBetRequestToBet(any())).thenReturn(Bet.builder().build());
        when(betRepository.save(any())).thenReturn(Mono.just(mappedBet));

        StepVerifier.create(betService.createBet(new CreateBetRequest()))
                .expectNext(mappedBet)
                .verifyComplete();

        verify(betMapper, times(1)).mapCreateBetRequestToBet(any());
        verify(betRepository, times(1)).save(any());
        verify(betKafkaService, times(1)).sendEntity(any());
    }

    @Test
    void whenGetBetByIdThenSuccess() {
        Bet expectedBet = Bet.builder().id(UUID.randomUUID()).build();
        when(betRepository.findById(any(UUID.class))).thenReturn(Mono.just(expectedBet));

        StepVerifier.create(betService.getBetById(UUID.randomUUID()))
                .expectNext(expectedBet)
                .verifyComplete();

        verify(betRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void whenGetAllBetsThenSuccess() {
        Bet bet1 = Bet.builder().id(UUID.randomUUID()).build();
        Bet bet2 = Bet.builder().id(UUID.randomUUID()).build();
        when(betRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(bet1, bet2));

        StepVerifier.create(betService.getAllBets(Pageable.unpaged()))
                .expectNext(bet1, bet2)
                .verifyComplete();

        verify(betRepository, times(1)).findAllBy(any(Pageable.class));
    }

    @Test
    void whenDeleteBetByIdThenSuccess() {
        when(betRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(betService.deleteBetById(UUID.randomUUID()))
                .verifyComplete();

        verify(betRepository, times(1)).deleteById(any(UUID.class));
    }

}
