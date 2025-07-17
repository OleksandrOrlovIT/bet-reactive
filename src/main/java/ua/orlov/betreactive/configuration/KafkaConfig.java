package ua.orlov.betreactive.configuration;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.messaging.Message; // Import Message
import org.springframework.messaging.support.MessageBuilder; // Import MessageBuilder
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ua.orlov.betreactive.model.User;
import com.fasterxml.jackson.databind.ObjectMapper; // For JSON serialization/deserialization

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@Log4j2
public class KafkaConfig {

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("user-topic")
                .partitions(10)
                .replicas(1)
                .build();
    }
}