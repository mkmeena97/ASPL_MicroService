package com.example.order_service.entity;

import com.example.common_events.Enums.InventoryStatus;
import com.example.common_events.Enums.OrderStatus;
import com.example.common_events.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_saga_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSagaState {

    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String product;

    private int quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
