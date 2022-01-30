package com.challenge1.item.controllers;

import com.challenge1.item.services.messaging.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class KafkaController {

    @Autowired
    private Producer producer;

    @GetMapping("/kafka/produce")
    public void produce(@RequestParam String message) {
        producer.sendMessage(message, "items");
    }

}