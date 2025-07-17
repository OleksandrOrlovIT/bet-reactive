package ua.orlov.betreactive.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;
import ua.orlov.betreactive.model.User;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ReactiveKafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ReactiveKafkaProducerTemplate<String, User> reactiveKafkaProducerTemplate() {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class);
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JsonSerializer.class);

        SenderOptions<String, User> senderOptions = SenderOptions.create(producerProps);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}