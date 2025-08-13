package com.example.order_service.kafka;

import com.example.common_events.Enums.InventoryStatus;
import com.example.common_events.Enums.OrderStatus;
import com.example.common_events.Enums.PaymentStatus;
import com.example.common_events.dto.InventoryEvent;
import com.example.common_events.dto.OrderEvent;
import com.example.common_events.dto.PaymentEvent;
import com.example.common_events.dto.OrderCancelEvent;
import com.example.common_events.dto.InventoryReleaseEvent;
import com.example.order_service.entity.OrderSagaState;
import com.example.order_service.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KafkaSagaListener {

    private final OrderSagaRepository orderSagaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate; // For publishing rollback events

    @KafkaListener(topics = "order-events", groupId = "order-group")
    public void handleOrderEvent(OrderEvent event) {
        OrderSagaState sagaState = orderSagaRepository.findById(event.getOrderId())
                .orElse(OrderSagaState.builder()
                        .orderId(event.getOrderId())
                        .product(event.getProduct())
                        .quantity(event.getQuantity())
                        .build());

        sagaState.setOrderStatus(event.getStatus());
        sagaState.setUpdatedAt(LocalDateTime.now());
        orderSagaRepository.save(sagaState);
    }

    @KafkaListener(topics = "inventory-events", groupId = "order-group")
    public void handleInventoryEvent(InventoryEvent event) {
        Optional<OrderSagaState> optionalState = orderSagaRepository.findById(event.getOrderId());
        if (optionalState.isPresent()) {
            OrderSagaState sagaState = optionalState.get();
            sagaState.setInventoryStatus(event.getStatus());

            if (event.getStatus() == InventoryStatus.INVENTORY_FAILED) {
                sagaState.setOrderStatus(OrderStatus.CANCELLED);
                // Publish compensating event to notify Payment & others
                OrderCancelEvent cancel = OrderCancelEvent.builder()
                        .orderId(event.getOrderId())
                        .reason("Inventory failed")
                        .build();
                kafkaTemplate.send("order-cancelled-events", event.getOrderId(), cancel);
            } else if (event.getStatus() == InventoryStatus.READY_FOR_PAYMENT) {
                sagaState.setOrderStatus(OrderStatus.PAYMENT_PENDING);
            }

            sagaState.setUpdatedAt(LocalDateTime.now());
            orderSagaRepository.save(sagaState);
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void handlePaymentEvent(PaymentEvent event) {
        Optional<OrderSagaState> optionalState = orderSagaRepository.findById(event.getOrderId());
        if (optionalState.isPresent()) {
            OrderSagaState sagaState = optionalState.get();
            sagaState.setPaymentStatus(event.getStatus());

            if (event.getStatus() == PaymentStatus.PAYMENT_SUCCESS) {
                sagaState.setOrderStatus(OrderStatus.COMPLETED);
            } else if (event.getStatus() == PaymentStatus.PAYMENT_FAILED) {
                sagaState.setOrderStatus(OrderStatus.CANCELLED);
                // Publish compensating event to release inventory
                InventoryReleaseEvent release = InventoryReleaseEvent.builder()
                        .orderId(event.getOrderId())
                        .product(sagaState.getProduct())
                        .quantity(sagaState.getQuantity())
                        .build();
                kafkaTemplate.send("inventory-release-events", event.getOrderId(), release);
            }

            sagaState.setUpdatedAt(LocalDateTime.now());
            orderSagaRepository.save(sagaState);
        }
    }
}
