package ua.orlov.betreactive.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import ua.orlov.betreactive.model.User;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserKafkaService implements CommandLineRunner {

    private final ReactiveKafkaProducerTemplate<String, User> kafkaProducerTemplate;
    private final ReceiverOptions<String, User> receiverOptions;
    private final ReactiveKafkaConsumerTemplate<String, User> reactiveKafkaConsumerTemplate;

    private final String topic = "user-topic";

    public void sendUser(User user) {
        log.info("Sending user to Kafka: {}", user);
        kafkaProducerTemplate.send(topic, user.getId().toString(), user)
                .doOnSuccess(result -> log.info("Sent successfully: {}", result.recordMetadata()))
                .doOnError(error -> log.error("Failed to send user", error))
                .subscribe();
    }

    public Flux<ReceiverRecord<String, User>> consumeUsers() {
        return KafkaReceiver.create(receiverOptions)
                .receive()
                .doOnNext(record -> {
                    log.info("Received User from Kafka: {}", record.value());
                    record.receiverOffset().acknowledge();
                });
    }

    @Override
    public void run(String... args) {
        consumeUsers().subscribe();
    }
}