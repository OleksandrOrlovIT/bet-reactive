package ua.orlov.betreactive.service.kafka;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderResult;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import reactor.test.StepVerifier;
import ua.orlov.betreactive.model.User;

@ExtendWith(MockitoExtension.class)
class UserKafkaServiceTest {

    @Mock
    ReactiveKafkaProducerTemplate<String, User> kafkaProducerTemplate;

    @Mock
    ReceiverOptions<String, User> receiverOptions;

    UserKafkaService userKafkaService;

    @BeforeEach
    void setup() {
        userKafkaService = new UserKafkaService(kafkaProducerTemplate, receiverOptions, "test-topic");
    }

    @Test
    void testSendUserSuccess() {
        User user = new User();
        user.setId(java.util.UUID.randomUUID());

        when(kafkaProducerTemplate.send(anyString(), anyString(), any(User.class)))
                .thenReturn(Mono.just(mock(SenderResult.class)));

        userKafkaService.sendUser(user);

        verify(kafkaProducerTemplate).send(eq("test-topic"), eq(user.getId().toString()), eq(user));
    }

    @Test
    void testSendUserFailure() {
        User user = new User();
        user.setId(java.util.UUID.randomUUID());

        when(kafkaProducerTemplate.send(anyString(), anyString(), any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Send failed")));

        userKafkaService.sendUser(user);

        verify(kafkaProducerTemplate).send(eq("test-topic"), eq(user.getId().toString()), eq(user));
    }

    @Test
    void testConsumeUsers() {
        ReceiverRecord<String, User> record = mock(ReceiverRecord.class);
        ReceiverOffset offset = mock(ReceiverOffset.class);
        User user = new User();
        user.setId(java.util.UUID.randomUUID());

        when(record.value()).thenReturn(user);
        when(record.receiverOffset()).thenReturn(offset);
        doNothing().when(offset).acknowledge();

        KafkaReceiver<String, User> kafkaReceiver = mock(KafkaReceiver.class);
        when(kafkaReceiver.receive()).thenReturn(Flux.just(record));
        try (MockedStatic<KafkaReceiver> kafkaReceiverStatic = mockStatic(KafkaReceiver.class)) {
            kafkaReceiverStatic.when(() -> KafkaReceiver.create(receiverOptions)).thenReturn(kafkaReceiver);

            Flux<ReceiverRecord<String, User>> flux = userKafkaService.consumeUsers();
            StepVerifier.create(flux)
                    .expectNext(record)
                    .verifyComplete();

            verify(record).receiverOffset();
            verify(offset).acknowledge();
        }
    }

    @Test
    void testRunCallsConsumeUsersSubscribe() {
        UserKafkaService spyService = spy(userKafkaService);
        doReturn(Flux.empty()).when(spyService).consumeUsers();

        spyService.run();

        verify(spyService).consumeUsers();
    }
}
