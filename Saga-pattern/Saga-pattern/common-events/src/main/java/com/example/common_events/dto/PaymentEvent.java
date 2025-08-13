package com.example.common_events.dto;

import com.example.common_events.Enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentEvent implements BaseEvent {


    private String eventType;
    private String orderId;
    private PaymentStatus status;    // Values: PAYMENT_SUCCESS or PAYMENT_FAILED
    private String product;
    private int quantity;

}

