package ua.orlov.betreactive.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;
import ua.orlov.betreactive.model.Bet;
import ua.orlov.betreactive.model.Event;
import ua.orlov.betreactive.model.User;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ReactiveKafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ReceiverOptions<String, User> userKafkaReceiverOptions(@Value("${kafka.topic.user}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, User> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, User> userReactiveKafkaConsumerTemplate(ReceiverOptions<String, User> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, User> userReactiveKafkaProducerTemplate() {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class);
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JsonSerializer.class);

        SenderOptions<String, User> senderOptions = SenderOptions.create(producerProps);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public ReceiverOptions<String, Event> eventKafkaReceiverOptions(@Value("${kafka.topic.event}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Event> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Event> eventReactiveKafkaConsumerTemplate(ReceiverOptions<String, Event> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, Event> eventReactiveKafkaProducerTemplate() {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class);
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JsonSerializer.class);

        SenderOptions<String, Event> senderOptions = SenderOptions.create(producerProps);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public ReceiverOptions<String, Bet> betKafkaReceiverOptions(@Value("${kafka.topic.bet}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Bet> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Bet> betReactiveKafkaConsumerTemplate(ReceiverOptions<String, Bet> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, Bet> betReactiveKafkaProducerTemplate() {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class);
        producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JsonSerializer.class);

        SenderOptions<String, Bet> senderOptions = SenderOptions.create(producerProps);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}
