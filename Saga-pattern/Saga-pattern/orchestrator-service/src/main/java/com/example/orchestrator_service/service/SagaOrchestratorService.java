package com.example.orchestrator_service.service;

import com.example.common_events.Enums.InventoryStatus;
import com.example.common_events.Enums.OrderStatus;
import com.example.common_events.Enums.PaymentStatus;
import com.example.common_events.dto.InventoryEvent;
import com.example.common_events.dto.OrderEvent;
import com.example.common_events.dto.PaymentEvent;
import com.example.common_events.dto.PaymentRequest;
import com.example.orchestrator_service.dto.OrderSagaState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaOrchestratorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // In-memory saga view (for quick trace). For production persist this.
    private final Map<String, OrderSagaState> sagaStore = new ConcurrentHashMap<>();

    /**
     * Kick off the saga by publishing OrderCreated on order-events.
     */
    public void initiateOrder(OrderEvent event) {
        log.info("Saga Start: Initiating Order ID: {}", event.getOrderId());
        event.setStatus(OrderStatus.PENDING);

        // store simple view
        OrderSagaState state = new OrderSagaState();
        state.setOrderId(event.getOrderId());
        state.setOrderEvent(event);
        state.setStatus(OrderStatus.PENDING);
        sagaStore.put(event.getOrderId(), state);

        CompletableFuture<?> future = kafkaTemplate.send("order-events", event.getOrderId(), event);
        future.whenComplete((r, ex) -> {
            if (ex == null) {
                log.info("Saga: OrderCreated published for {}", event.getOrderId());
            } else {
                log.error("Saga: Failed to publish OrderCreated for {}: {}", event.getOrderId(), ex.getMessage(), ex);
                // optional: mark saga as FAILED_LOCALLY
                state.updateStatus(OrderStatus.FAILED);
            }
        });
    }

    /**
     * Handle inventory responses.
     * If READY_FOR_PAYMENT -> publish PaymentRequest.
     * If INVENTORY_FAILED -> publish OrderCancelled.
     */
    public void handleInventoryResponse(InventoryEvent event) {
        log.info("Saga: Received InventoryEvent for order {} -> {}", event.getOrderId(), event.getStatus());

        OrderSagaState state = sagaStore.computeIfAbsent(event.getOrderId(), id -> {
            OrderSagaState s = new OrderSagaState();
            s.setOrderId(id);
            return s;
        });
        state.setInventoryEvent(event);

        if (event.getStatus() == InventoryStatus.READY_FOR_PAYMENT) {
            // Inventory is OK -> ask payment service to process payment
            state.updateStatus(OrderStatus.PAYMENT_PENDING);

            PaymentEvent paymentEvent = PaymentEvent.builder()
                    .eventType("PaymentEvent") // REQUIRED for polymorphic deserialization
                    .orderId(event.getOrderId())
                    .product(event.getProduct())
                    .quantity(event.getQuantity())
                    .status(PaymentStatus.PENDING)
                    .build();

            CompletableFuture<?> fut = kafkaTemplate.send("payment-requests", paymentEvent.getOrderId(), paymentEvent);
            fut.whenComplete((r, ex) -> {
                if (ex == null) {
                    log.info("Saga: Sent PaymentEvent(PENDING) to payment-requests for order {}", paymentEvent.getOrderId());
                } else {
                    log.error("Saga: Failed to send PaymentEvent for {}: {}", paymentEvent.getOrderId(), ex.getMessage(), ex);
                    state.updateStatus(OrderStatus.FAILED);
                    sendOrderCancelled(event.getOrderId(), "Failed to send payment request");
                }
            });

        } else if (event.getStatus() == InventoryStatus.INVENTORY_FAILED) {
            // Inventory failed -> cancel order
            state.updateStatus(OrderStatus.CANCELLED);
            sendOrderCancelled(event.getOrderId(), "Inventory failed");
        } else {
            state.updateStatus(OrderStatus.FAILED);
        }
    }

    /**
     * Handle payment responses.
     * If payment success -> publish order completed event.
     * If payment failed -> publish order cancelled + inventory release.
     */
    public void handlePaymentResponse(PaymentEvent event) {
        log.info("Saga: Received PaymentEvent for order {} -> {}", event.getOrderId(), event.getStatus());

        OrderSagaState state = sagaStore.computeIfAbsent(event.getOrderId(), id -> {
            OrderSagaState s = new OrderSagaState();
            s.setOrderId(id);
            return s;
        });
        state.setPaymentEvent(event);

        if (event.getStatus() == PaymentStatus.PAYMENT_SUCCESS) {
            state.updateStatus(OrderStatus.COMPLETED);

            OrderEvent orderEvent = OrderEvent.builder()
                    .orderId(event.getOrderId())
                    .product(event.getProduct())
                    .quantity(event.getQuantity())
                    .status(OrderStatus.COMPLETED)
                    .build();

            kafkaTemplate.send("order-events", orderEvent.getOrderId(), orderEvent)
                    .whenComplete((r, ex) -> {
                        if (ex == null) log.info("Saga: Published Order COMPLETED for {}", orderEvent.getOrderId());
                        else log.error("Saga: Failed publishing Order COMPLETED for {}: {}", orderEvent.getOrderId(), ex.getMessage());
                    });

        } else if (event.getStatus() == PaymentStatus.PAYMENT_FAILED) {
            state.updateStatus(OrderStatus.CANCELLED);

            // notify order cancelled
            sendOrderCancelled(event.getOrderId(), "Payment failed");

            // request inventory release
            sendInventoryRelease(event.getOrderId(), state.getOrderEvent() != null ? state.getOrderEvent().getProduct() : event.getProduct(),
                    state.getOrderEvent() != null ? state.getOrderEvent().getQuantity() : event.getQuantity());
        } else {
            state.updateStatus(OrderStatus.FAILED);
        }
    }

    private void sendOrderCancelled(String orderId, String reason) {
        try {
            // OrderCancelEvent DTO
            com.example.common_events.dto.OrderCancelEvent cancel = com.example.common_events.dto.OrderCancelEvent.builder()
                    .orderId(orderId)
                    .reason(reason)
                    .build();

            kafkaTemplate.send("order-cancelled-events", orderId, cancel)
                    .whenComplete((r, ex) -> {
                        if (ex == null) log.info("Saga: Published OrderCancelEvent for {}", orderId);
                        else log.error("Saga: Failed to publish OrderCancelEvent for {}: {}", orderId, ex.getMessage());
                    });
        } catch (Exception e) {
            log.error("Saga: Exception while sending OrderCancelEvent for {}: {}", orderId, e.getMessage(), e);
        }
    }

    private void sendInventoryRelease(String orderId, String product, int quantity) {
        try {
            com.example.common_events.dto.InventoryReleaseEvent release = com.example.common_events.dto.InventoryReleaseEvent.builder()
                    .orderId(orderId)
                    .product(product)
                    .quantity(quantity)
                    .build();

            kafkaTemplate.send("inventory-release-events", orderId, release)
                    .whenComplete((r, ex) -> {
                        if (ex == null) log.info("Saga: Published InventoryReleaseEvent for {}", orderId);
                        else log.error("Saga: Failed to publish InventoryReleaseEvent for {}: {}", orderId, ex.getMessage());
                    });
        } catch (Exception e) {
            log.error("Saga: Exception while sending InventoryReleaseEvent for {}: {}", orderId, e.getMessage(), e);
        }
    }

    // For debugging / tracing
    public OrderSagaState getSagaState(String orderId) {
        return sagaStore.get(orderId);
    }

    public java.util.Collection<OrderSagaState> getAllStates() {
        return sagaStore.values();
    }
}
