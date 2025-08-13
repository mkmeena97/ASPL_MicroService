package com.example.common_events.dto;


import com.example.common_events.Enums.OrderStatus;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderEvent implements BaseEvent {
    private String orderId;
    private String product;
    private int quantity;
    private OrderStatus status; // e.g. CREATED, FAILED, COMPLETED
}

