package iam.shoukou.kafkaexample.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSimpleProducerService {

    private final String PRODUCER_PREFIX = "[Producer] ";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaSimpleProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        this.kafkaTemplate.send("shoukou", PRODUCER_PREFIX + message);
    }
}
