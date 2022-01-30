package com.challenge1.item.services.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private static final String DEFAULT_TOPIC = "DEFAULT";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        LOGGER.info(String.format("#### -> Producing message -> %s from %s", message, DEFAULT_TOPIC));
        this.kafkaTemplate.send(DEFAULT_TOPIC, message);
    }

    public void sendMessage(String message, String topic) {
        LOGGER.info(String.format("#### -> Producing message -> %s from %s", message, topic));
        this.kafkaTemplate.send(topic, message);
    }
}
