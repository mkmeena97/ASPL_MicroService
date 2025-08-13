package com.example.payment_service.kafka;

import com.example.common_events.dto.OrderCancelEvent;
import com.example.payment_service.entity.PaymentRequest;
import com.example.payment_service.repository.PaymentRequestRepository;
import com.example.common_events.Enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompensationListener {

    private static final Logger log = LoggerFactory.getLogger(CompensationListener.class);
    private final PaymentRequestRepository paymentRequestRepository;

    @KafkaListener(topics = "order-cancelled-events", groupId = "payment-group")
    @Transactional
    public void onOrderCancelled(OrderCancelEvent event) {
        Optional<PaymentRequest> opt = paymentRequestRepository.findById(event.getOrderId());
        if (opt.isPresent()) {
            PaymentRequest req = opt.get();
            if (req.getStatus() == PaymentStatus.PENDING) {
                req.setStatus(PaymentStatus.CANCELLED);
                paymentRequestRepository.save(req);
                log.info("Cancelled pending payment for order {} (reason: {})", event.getOrderId(), event.getReason());
            } else {
                log.info("OrderCancelEvent for order {} ignored; payment status is {}", event.getOrderId(), req.getStatus());
            }
        } else {
            log.warn("OrderCancelEvent for unknown order {}", event.getOrderId());
        }
    }
}

