package ua.orlov.betreactive.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.orlov.betreactive.configuration.ReactiveKafkaConfig;
import ua.orlov.betreactive.model.User;

@Service
public class UserKafkaService extends KafkaService<User> {

    public UserKafkaService(ReactiveKafkaConfig kafkaConfig, @Value("${kafka.topic.user}") String topic) {
        super(
                kafkaConfig.createProducerTemplate(User.class),
                kafkaConfig.createReceiverOptions(topic),
                topic,
                "User"
        );
    }
}
