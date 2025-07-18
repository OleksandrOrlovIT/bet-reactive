package ua.orlov.betreactive.service.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import ua.orlov.betreactive.model.BaseEntity;

@Log4j2
public abstract class KafkaService<T extends BaseEntity> implements CommandLineRunner {

    private final ReactiveKafkaProducerTemplate<String, T> producerTemplate;
    private final ReceiverOptions<String, T> receiverOptions;
    private final String topic;

    private final String sendSuccess;
    private final String sendError;
    private final String receiveSuccess;

    public KafkaService(ReactiveKafkaProducerTemplate<String, T> producerTemplate,
                        ReceiverOptions<String, T> receiverOptions,
                        String topic,
                        String entityName) {
        this.producerTemplate = producerTemplate;
        this.receiverOptions = receiverOptions;
        this.topic = topic;

        this.sendSuccess = entityName + " sent to Kafka: {}";
        this.sendError = "Failed to send " + entityName + " to Kafka";
        this.receiveSuccess = "Received " + entityName + " from Kafka: {}";
    }

    public void sendEntity(T entity) {
        producerTemplate.send(topic, entity.getId().toString(), entity)
                .doOnSuccess(r -> log.info(sendSuccess, entity))
                .doOnError(e -> log.error(sendError, e))
                .subscribe();
    }

    public Flux<ReceiverRecord<String, T>> consumeEntity() {
        return KafkaReceiver.create(receiverOptions)
                .receive()
                .doOnNext(record -> {
                    log.info(receiveSuccess, record.value());
                    record.receiverOffset().acknowledge();
                });
    }

    @Override
    public void run(String... args) {
        consumeEntity().subscribe();
    }
}
