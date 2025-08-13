package com.example.common_events.dto;


import com.example.common_events.Enums.InventoryStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InventoryEvent implements BaseEvent {
    private String eventType;
    private String orderId;
    private String product;
    private int quantity;
    private InventoryStatus status; // IN_PROGRESS, INVENTORY_FAILED, READY_FOR_PAYMENT
}
