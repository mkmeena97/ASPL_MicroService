package com.example.common_events.dto;


import lombok.*;

/**
 * Event sent by Order Service to signal that an order should be cancelled.
 * Can be triggered when inventory fails or payment fails.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCancelEvent {
    private String orderId;
    private String reason;
}

