package com.example.payment_service.service;

import com.example.common_events.Enums.PaymentStatus;
import com.example.common_events.entity.Outbox;
import com.example.common_events.dto.OutboxStatus;
import com.example.common_events.dto.PaymentEvent;
import com.example.common_events.repository.OutboxRepository;
import com.example.payment_service.kafka.OutboxPublisher;
import com.example.payment_service.repository.PaymentRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRequestRepository paymentRequestRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public boolean confirmPayment(String orderId) {
        return updatePaymentStatus(orderId, PaymentStatus.PAYMENT_SUCCESS, "PaymentSuccess");
    }

    @Transactional
    public boolean rejectPayment(String orderId) {
        boolean updated = updatePaymentStatus(orderId, PaymentStatus.PAYMENT_FAILED, "PaymentFailed");
        // Order service will listen to PaymentFailed and release inventory; Order service handles compensations further.
        return updated;
    }

    private boolean updatePaymentStatus(String orderId, PaymentStatus newStatus, String eventType) {
        return paymentRequestRepository.findById(orderId).map(request -> {
            if (request.getStatus() != com.example.common_events.Enums.PaymentStatus.PENDING) {
                log.warn("Payment for order {} is not PENDING (status={}), ignoring update to {}", orderId, request.getStatus(), newStatus);
                return false;
            }

            request.setStatus(newStatus);
            paymentRequestRepository.save(request);

            PaymentEvent event = PaymentEvent.builder()
                    .orderId(orderId)
                    .product(request.getProduct())
                    .quantity(request.getQuantity())
                    .status(newStatus)
                    .build();

            Outbox outbox = Outbox.builder()
                    .id(UUID.randomUUID().toString())
                    .aggregateType("payment")
                    .eventType(eventType)
                    .payload(serialize(event))
                    .status(OutboxStatus.PENDING)
                    .attemptCount(0)
                    .build();

            outbox = outboxRepository.save(outbox);
            outboxPublisher.publishSingle(outbox);

            log.info("Updated payment status to {} and queued outbox event {}", newStatus, eventType);
            return true;
        }).orElse(false);
    }

    private String serialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
