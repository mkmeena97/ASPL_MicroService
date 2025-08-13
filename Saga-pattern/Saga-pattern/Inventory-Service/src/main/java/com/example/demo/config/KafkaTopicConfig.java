package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderEventsTopic() {
        return new NewTopic("order-events", 1, (short) 1);
    }

    @Bean
    public NewTopic inventoryEventsTopic() {
        return new NewTopic("inventory-events", 1, (short) 1);
    }

    // Compensating / control topics
    @Bean
    public NewTopic inventoryReleaseTopic() {
        return new NewTopic("inventory-release-events", 1, (short) 1);
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return new NewTopic("order-cancelled-events", 1, (short) 1);
    }
}
