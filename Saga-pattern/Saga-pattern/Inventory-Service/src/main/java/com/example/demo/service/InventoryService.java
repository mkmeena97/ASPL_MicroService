package com.example.demo.service;

import com.example.common_events.Enums.InventoryStatus;
import com.example.common_events.dto.InventoryEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public void publishInventoryEvent(String orderId, String product, int quantity, InventoryStatus status) {
        InventoryEvent event = InventoryEvent.builder()
                .orderId(orderId)
                .product(product)
                .quantity(quantity)
                .status(status)
                .build();

        CompletableFuture<?> future = kafkaTemplate.send("inventory-events", orderId, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(" InventoryEvent published successfully for order {} with status {}", orderId, status);
            } else {
                log.error(" Failed to publish InventoryEvent for order {}: {}", orderId, ex.getMessage(), ex);
                // In real-world, retry or store in outbox for later send
            }
        });
    }
}
