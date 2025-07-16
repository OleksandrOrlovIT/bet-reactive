package ua.orlov.betreactive.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public Sinks.Many<Message<String>> userSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    public void send(Sinks.Many<Message<String>> sink, String payload) {
        Message<String> message = MessageBuilder.withPayload(payload).build();
        sink.tryEmitNext(message);
    }

    @Bean
    public Supplier<Flux<Message<String>>> userProducer(Sinks.Many<Message<String>> userSink) {
        return userSink::asFlux;
    }
}
