package ua.orlov.betreactive.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@Log4j2
public class KafkaConsumerConfig {

    @Bean
    public Consumer<Flux<Message<String>>> userConsumer() {
        return messageFlux -> messageFlux
                .doOnNext(message -> log.info("Received Kafka message: {}", message.getPayload()))
                .subscribe();
    }
}
