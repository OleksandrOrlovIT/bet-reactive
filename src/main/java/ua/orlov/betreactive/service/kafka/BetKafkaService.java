package ua.orlov.betreactive.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.orlov.betreactive.configuration.ReactiveKafkaConfig;
import ua.orlov.betreactive.model.Bet;

@Service
public class BetKafkaService extends KafkaService<Bet> {

    public BetKafkaService(ReactiveKafkaConfig kafkaConfig, @Value("${kafka.topic.bet}") String topic) {
        super(
                kafkaConfig.createProducerTemplate(Bet.class),
                kafkaConfig.createReceiverOptions(topic),
                topic,
                "Bet"
        );
    }
}
