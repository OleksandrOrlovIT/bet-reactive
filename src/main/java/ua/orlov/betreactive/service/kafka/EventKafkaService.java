package ua.orlov.betreactive.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.ReceiverOptions;
import ua.orlov.betreactive.model.Event;

@Service
public class EventKafkaService  extends KafkaService<Event> {
    public EventKafkaService(
            ReactiveKafkaProducerTemplate<String, Event> kafkaProducerTemplate,
            ReceiverOptions<String, Event> receiverOptions,
            @Value("${kafka.topic.event}") String topic
    ) {
        super(kafkaProducerTemplate, receiverOptions, topic, "Event");
    }
}
