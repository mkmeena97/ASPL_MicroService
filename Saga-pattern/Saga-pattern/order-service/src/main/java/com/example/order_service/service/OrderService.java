package com.example.order_service.service;

import com.example.common_events.Enums.OrderStatus;
import com.example.common_events.dto.OrderEvent;
import com.example.common_events.entity.Outbox;
import com.example.common_events.dto.OutboxStatus;
import com.example.common_events.repository.OutboxRepository;
import com.example.order_service.entity.OrderSagaState;
import com.example.order_service.kafka.OutboxPublisher;
import com.example.order_service.repository.OrderSagaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderSagaRepository orderSagaRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public String publishOrder(OrderEvent event) {
        String orderId = event.getOrderId();

        // Persist initial saga state
        OrderSagaState sagaState = OrderSagaState.builder()
                .orderId(orderId)
                .product(event.getProduct())
                .quantity(event.getQuantity())
                .orderStatus(OrderStatus.PENDING)
                .inventoryStatus(null)
                .paymentStatus(null)
                .build();

        orderSagaRepository.save(sagaState);

        // Create outbox entry in same transaction
        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateType("order")
                .eventType("OrderCreated")
                .payload(serialize(event))
                .status(OutboxStatus.PENDING)
                .attemptCount(0)
                .build();

        outbox = outboxRepository.save(outbox);

        // Publish immediately (hybrid approach)
        outboxPublisher.publishSingle(outbox);

        return orderId;
    }

    private String serialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
