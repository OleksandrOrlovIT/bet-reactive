package ua.orlov.betreactive.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.orlov.betreactive.configuration.ReactiveKafkaConfig;
import ua.orlov.betreactive.model.Event;

@Service
public class EventKafkaService  extends KafkaService<Event> {
    public EventKafkaService(ReactiveKafkaConfig kafkaConfig, @Value("${kafka.topic.event}") String topic) {
        super(
                kafkaConfig.createProducerTemplate(Event.class),
                kafkaConfig.createReceiverOptions(topic),
                topic,
                "Event");
    }
}
