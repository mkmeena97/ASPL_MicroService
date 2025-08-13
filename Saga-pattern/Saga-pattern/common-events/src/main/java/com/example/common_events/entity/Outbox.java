package com.example.common_events.entity;


import com.example.common_events.dto.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox {

    @Id
    private String id;

    // e.g. "order", "inventory", "payment"
    private String aggregateType;

    // e.g. "OrderCreated", "InventoryReserved", "PaymentSuccess", "PaymentFailed", "OrderCancelled"
    private String eventType;

    // JSON payload of the event DTO
    @Lob
    @Column(columnDefinition = "CLOB")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private int attemptCount;

    @PrePersist
    public void createdAt() {
        this.createdAt = LocalDateTime.now();
    }
}

