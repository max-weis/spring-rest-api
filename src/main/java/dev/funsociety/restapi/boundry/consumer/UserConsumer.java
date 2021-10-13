package dev.funsociety.restapi.boundry.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserConsumer {

    @KafkaListener(topics = "user", groupId = "users")
    public void listenGroupFoo(String message) {
        log.info("Received User: {}", message);
    }

}
