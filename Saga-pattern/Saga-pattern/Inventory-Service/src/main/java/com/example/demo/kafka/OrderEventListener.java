package com.example.demo.kafka;

import com.example.common_events.Enums.InventoryStatus;
import com.example.common_events.dto.OrderEvent;
import com.example.demo.entity.InventoryRequest;
import com.example.demo.repository.InventoryRequestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final InventoryRequestRepository inventoryRequestRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void consume(ConsumerRecord<String, OrderEvent> record) {
        OrderEvent event = record.value();

        if (inventoryRequestRepository.existsById(event.getOrderId())) {
            log.warn(" Duplicate order {} received, skipping.", event.getOrderId());
            return;
        }

        InventoryRequest request = InventoryRequest.builder()
                .orderId(event.getOrderId())
                .product(event.getProduct())
                .quantity(event.getQuantity())
                .status(InventoryStatus.PENDING)
                .build();

        inventoryRequestRepository.save(request);
        log.info(" New inventory request stored for order {}", event.getOrderId());
    }
}
