package com.example.common_events.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event sent by Order Service to Inventory Service
 * to release previously reserved stock when an order fails or is cancelled.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryReleaseEvent {
    private String orderId;
    private String product;
    private int quantity;
}

