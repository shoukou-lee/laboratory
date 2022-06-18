package iam.shoukou.kafkaexample.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSimpleConsumerService {
    private final String CONSUMER_PREFIX = "[Consumer] ";

    @KafkaListener(topics = "shoukou", groupId = "group-id-shoukou")
    public void consume(String message) {
        System.out.println(CONSUMER_PREFIX + message);
    }

}
