package com.example.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String PAYMENT_EVENTS = "payment-events";
    public static final String PAYMENT_REQUESTS = "payment-requests";
    public static final String ORDER_CANCELLED = "order-cancelled-events";
    public static final String INVENTORY_RELEASE = "inventory-release-events";

    @Bean
    public NewTopic paymentEvents() {
        return TopicBuilder.name(PAYMENT_EVENTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentRequests() {
        return TopicBuilder.name(PAYMENT_REQUESTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name(ORDER_CANCELLED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryReleaseTopic() {
        return TopicBuilder.name(INVENTORY_RELEASE).partitions(1).replicas(1).build();
    }
}
