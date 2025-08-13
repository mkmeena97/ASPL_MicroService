package com.example.common_events.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentRequest {
    private String orderId;
    private String product;
    private int quantity;
}

