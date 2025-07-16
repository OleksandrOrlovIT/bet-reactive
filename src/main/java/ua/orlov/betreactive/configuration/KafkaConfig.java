package ua.orlov.betreactive.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.test.TestUtils.consumerConfig;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("user.create")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate() {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions());
    }

    private ReceiverOptions<String, String> receiverOptions() {
        Map<String, Object> consumerConfig = new HashMap<>();
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.create(consumerConfig);
        return receiverOptions.subscription(Collections.singletonList("user.create"));
    }

}
