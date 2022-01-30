package com.challenge1.item.components;

import com.challenge1.item.services.messaging.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TopicConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicConsumer.class);

    @KafkaListener(topics = "items", groupId = "kafka-sandbox")
    public void listen(String message) {
        LOGGER.info(String.format("#### -> Producing message -> %s from %s", message, "items"));
    }
}
