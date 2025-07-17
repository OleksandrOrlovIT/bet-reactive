package ua.orlov.betreactive.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import ua.orlov.betreactive.model.User;

import java.util.Collections;

@Configuration
public class ReactiveKafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, User> kafkaReceiverOptions(@Value("${user.topic}") String topic,
                                                              KafkaProperties kafkaProperties) {
        ReceiverOptions<String, User> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, User> reactiveKafkaConsumerTemplate(ReceiverOptions<String, User> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}