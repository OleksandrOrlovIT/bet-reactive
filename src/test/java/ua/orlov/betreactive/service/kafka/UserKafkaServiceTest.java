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
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;
import ua.orlov.betreactive.configuration.ReactiveKafkaConfig;
import ua.orlov.betreactive.model.User;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserKafkaServiceTest {

    @Mock
    private ReactiveKafkaConfig kafkaConfig;

    @Mock
    private ReactiveKafkaProducerTemplate<String, User> kafkaProducerTemplate;

    @Mock
    private ReceiverOptions<String, User> receiverOptions;

    private UserKafkaService userKafkaService;

    @BeforeEach
    void setup() {
        when(kafkaConfig.createProducerTemplate(User.class)).thenReturn(kafkaProducerTemplate);

        when(kafkaConfig.<User>createReceiverOptions("test-topic")).thenReturn(receiverOptions);

        userKafkaService = new UserKafkaService(kafkaConfig, "test-topic");
    }

    @Test
    void testSendUserSuccess() {
        User user = User.builder().id(UUID.randomUUID()).build();

        when(kafkaProducerTemplate.send(anyString(), anyString(), any(User.class)))
                .thenReturn(Mono.just(mock(SenderResult.class)));

        userKafkaService.sendEntity(user);

        verify(kafkaProducerTemplate).send(eq("test-topic"), eq(user.getId().toString()), eq(user));
    }

    @Test
    void testSendUserFailure() {
        User user = User.builder().id(UUID.randomUUID()).build();

        when(kafkaProducerTemplate.send(anyString(), anyString(), any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Send failed")));

        userKafkaService.sendEntity(user);

        verify(kafkaProducerTemplate).send(eq("test-topic"), eq(user.getId().toString()), eq(user));
    }

    @Test
    void testConsumeUsers() {
        ReceiverRecord<String, User> record = mock(ReceiverRecord.class);
        ReceiverOffset offset = mock(ReceiverOffset.class);
        User user = User.builder().id(UUID.randomUUID()).build();

        when(record.value()).thenReturn(user);
        when(record.receiverOffset()).thenReturn(offset);
        doNothing().when(offset).acknowledge();

        KafkaReceiver<String, User> kafkaReceiver = mock(KafkaReceiver.class);
        when(kafkaReceiver.receive()).thenReturn(Flux.just(record));

        try (MockedStatic<KafkaReceiver> kafkaReceiverStatic = mockStatic(KafkaReceiver.class)) {
            kafkaReceiverStatic.when(() -> KafkaReceiver.create(receiverOptions)).thenReturn(kafkaReceiver);

            Flux<ReceiverRecord<String, User>> flux = userKafkaService.consumeEntity();

            StepVerifier.create(flux)
                    .expectNext(record)
                    .verifyComplete();

            verify(record).receiverOffset();
            verify(offset).acknowledge();
        }
    }

    @Test
    void testRunCallsConsumeEntitySubscribe() {
        UserKafkaService spyService = spy(userKafkaService);
        doReturn(Flux.empty()).when(spyService).consumeEntity();

        spyService.run();

        verify(spyService).consumeEntity();
    }
}
