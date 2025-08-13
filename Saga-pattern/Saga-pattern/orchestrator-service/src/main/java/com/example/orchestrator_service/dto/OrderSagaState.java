package com.example.orchestrator_service.dto;

import com.example.common_events.Enums.OrderStatus;
import com.example.common_events.dto.InventoryEvent;
import com.example.common_events.dto.OrderEvent;
import com.example.common_events.dto.PaymentEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaState {

    private String orderId;
    private OrderStatus status; // uses enum
    private OrderEvent orderEvent;
    private InventoryEvent inventoryEvent;
    private PaymentEvent paymentEvent;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
