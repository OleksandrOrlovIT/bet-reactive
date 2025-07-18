package ua.orlov.betreactive.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.ReceiverOptions;
import ua.orlov.betreactive.model.Bet;

@Service
public class BetKafkaService extends KafkaService<Bet> {
    public BetKafkaService(ReactiveKafkaProducerTemplate<String, Bet> reactiveKafkaProducerTemplate,
                           ReceiverOptions<String, Bet> receiverOptions,
                           @Value("${kafka.topic.bet}") String topic) {
        super(
                reactiveKafkaProducerTemplate,
                receiverOptions,
                topic,
                "Bet"
        );
    }
}
