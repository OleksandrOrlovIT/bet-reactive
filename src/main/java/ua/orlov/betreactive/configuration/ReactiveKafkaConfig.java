package ua.orlov.betreactive.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ReactiveKafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ReactiveKafkaProducerTemplate<String, String> stringKafkaProducerTemplate() {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerProps));
    }

    public <T> ReactiveKafkaProducerTemplate<String, T> createProducerTemplate(Class<T> clazz) {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerProps));
    }

    public <T> ReceiverOptions<String, T> createReceiverOptions(String topic) {
        return ReceiverOptions.<String, T>create(kafkaProperties.buildConsumerProperties())
                .subscription(Collections.singletonList(topic));
    }
}
