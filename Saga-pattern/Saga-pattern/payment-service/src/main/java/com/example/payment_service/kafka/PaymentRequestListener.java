package com.example.payment_service.kafka;

import com.example.common_events.dto.PaymentEvent;
import com.example.common_events.Enums.PaymentStatus;
import com.example.payment_service.entity.PaymentRequest;
import com.example.payment_service.repository.PaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestListener.class);

    private final PaymentRequestRepository paymentRequestRepository;

    @KafkaListener(topics = "${com.example.kafka.topics.payment-requests:payment-requests}", groupId = "payment-group")
    @Transactional
    public void handlePaymentRequest(PaymentEvent event) {
        String orderId = event.getOrderId();
        paymentRequestRepository.findById(orderId).ifPresentOrElse(existing -> {
            log.warn("PaymentRequest for order {} already exists with status {}. Ignoring.", orderId, existing.getStatus());
        }, () -> {
            PaymentRequest entity = PaymentRequest.builder()

                    .orderId(orderId)
                    .product(event.getProduct())
                    .quantity(event.getQuantity())
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRequestRepository.save(entity);
            log.info("Saved PaymentRequest as PENDING for order: {}", orderId);
        });
    }
}
