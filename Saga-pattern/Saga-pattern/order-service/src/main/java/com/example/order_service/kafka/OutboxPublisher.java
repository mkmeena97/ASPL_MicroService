package com.example.order_service.kafka;

import com.example.common_events.entity.Outbox;
import com.example.common_events.dto.OutboxStatus;
import com.example.common_events.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final int MAX_ATTEMPTS = 3;

    // Retry publisher (runs on schedule)
    @Scheduled(fixedDelayString = "${outbox.publisher.delay:15000}")
    @Transactional
    public void publishPending() {
        List<Outbox> pending = outboxRepository.findPendingForProcessing();
        for (Outbox outbox : pending) {
            publishSingle(outbox);
        }
    }

    // Can be called immediately after saving outbox
    @Transactional
    public void publishSingle(Outbox outbox) {
        try {
            if (outbox.getAttemptCount() >= MAX_ATTEMPTS) {
                markAsFailed(outbox);
                return;
            }

            String topic = mapEventToTopic(outbox.getEventType(), outbox.getAggregateType());
            CompletableFuture<?> future = kafkaTemplate.send(
                    topic,
                    outbox.getId(),
                    objectMapper.readTree(outbox.getPayload())
            );

            future.whenComplete((sendResult, ex) -> {
                if (ex == null) {
                    markAsSent(outbox.getId(), topic);
                } else {
                    incrementAttempt(outbox.getId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Exception processing outbox {}: {}", outbox.getId(), e.getMessage(), e);
        }
    }

    private void markAsSent(String outboxId, String topic) {
        outboxRepository.findById(outboxId).ifPresent(o -> {
            o.setStatus(OutboxStatus.SENT);
            o.setProcessedAt(LocalDateTime.now());
            outboxRepository.save(o);
            log.info("Outbox {} published to topic {}", outboxId, topic);
        });
    }

    private void incrementAttempt(String outboxId, Throwable ex) {
        outboxRepository.findById(outboxId).ifPresent(o -> {
            o.setAttemptCount(o.getAttemptCount() + 1);
            outboxRepository.save(o);
            log.error("Failed to publish outbox {}: {}", outboxId, ex.getMessage());
        });
    }

    private void markAsFailed(Outbox outbox) {
        outbox.setStatus(OutboxStatus.FAILED);
        outbox.setProcessedAt(LocalDateTime.now());
        outboxRepository.save(outbox);
        log.error("Outbox {} exceeded max attempts, marking FAILED", outbox.getId());
    }

    private String mapEventToTopic(String eventType, String aggregateType) {
        switch (eventType) {
            case "OrderCreated":
            case "OrderCancelled": return "order-events";
            case "InventoryReserved":
            case "InventoryFailed":
            case "InventoryReleased": return "inventory-events";
            case "PaymentSuccess":
            case "PaymentFailed": return "payment-events";
            default: return aggregateType + "-events";
        }
    }
}
