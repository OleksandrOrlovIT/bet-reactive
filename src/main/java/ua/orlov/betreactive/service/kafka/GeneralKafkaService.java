package ua.orlov.betreactive.service.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import ua.orlov.betreactive.configuration.ReactiveKafkaConfig;

@Log4j2
@Service
public class GeneralKafkaService implements CommandLineRunner {

    private final ReactiveKafkaProducerTemplate<String, String> producerTemplate;
    private final ReceiverOptions<String, String> receiverOptions;

    private final String generalTopic;

    public GeneralKafkaService(ReactiveKafkaConfig config, @Value("${kafka.topic.general}") String topic) {
        this.producerTemplate = config.createProducerTemplate(String.class);
        this.receiverOptions = config.createReceiverOptions(topic);
        this.generalTopic = topic;
    }

    public Mono<Void> send(String topic, String key, String message) {
        return producerTemplate.send(topic, key, message)
                .doOnSuccess(r -> log.info("Sent to topic [{}]: {}", topic, message))
                .doOnError(e -> log.error("Failed to send to [{}]: {}", topic, message, e))
                .then();
    }

    public Mono<Void> sendToGeneral(String key, String message) {
        return producerTemplate.send(generalTopic, key, message)
                .doOnSuccess(r -> log.info("Sent to topic [{}]: {}", generalTopic, message))
                .doOnError(e -> log.error("Failed to send to [{}]: {}", generalTopic, message, e))
                .then();
    }

    public Flux<ReceiverRecord<String, String>> consumeString() {
        return KafkaReceiver.create(receiverOptions)
                .receive()
                .doOnNext(record -> {
                    log.info("Received message: {}", record.value());
                    record.receiverOffset().acknowledge();
                });
    }

    @Override
    public void run(String... args) {
        consumeString().subscribe();
    }
}
