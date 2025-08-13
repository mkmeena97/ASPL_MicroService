package com.example.demo.kafka;

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

    private static final int MAX_ATTEMPTS = 3; // Retry limit

    /**
     * Scheduled retry publisher
     */
    @Scheduled(fixedDelayString = "${outbox.publisher.delay:15000}")
    @Transactional
    public void publishPending() {
        List<Outbox> pending = outboxRepository.findPendingForProcessing();

        for (Outbox outbox : pending) {
            publishSingle(outbox);
        }
    }

    /**
     * Immediate publish method - can be called right after saving Outbox
     */
    @Transactional
    public void publishSingle(Outbox outbox) {
        try {
            if (outbox.getAttemptCount() >= MAX_ATTEMPTS) {
                outbox.setStatus(OutboxStatus.FAILED);
                outbox.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(outbox);
                log.error("Outbox {} exceeded max attempts, marking FAILED", outbox.getId());
                return;
            }

            String topic = mapEventToTopic(outbox.getEventType(), outbox.getAggregateType());

            CompletableFuture<?> future = kafkaTemplate.send(
                    topic,
                    outbox.getId(),
                    objectMapper.readTree(outbox.getPayload())
            );

            final String outboxId = outbox.getId();
            future.whenComplete((sendResult, ex) -> {
                if (ex == null) {
                    try {
                        Outbox o = outboxRepository.findById(outboxId).orElse(null);
                        if (o != null) {
                            o.setStatus(OutboxStatus.SENT);
                            o.setProcessedAt(LocalDateTime.now());
                            outboxRepository.save(o);
                        }
                        log.info("Outbox {} published to topic {}", outboxId, topic);
                    } catch (Exception e) {
                        log.error("Error updating outbox after send: {}", e.getMessage(), e);
                    }
                } else {
                    try {
                        Outbox o = outboxRepository.findById(outboxId).orElse(null);
                        if (o != null) {
                            o.setAttemptCount(o.getAttemptCount() + 1);
                            outboxRepository.save(o);
                        }
                        log.error("Failed to publish outbox {}: {}", outboxId, ex.getMessage());
                    } catch (Exception e) {
                        log.error("Error updating outbox attemptCount: {}", e.getMessage(), e);
                    }
                }
            });

        } catch (Exception e) {
            log.error("Exception processing outbox {}: {}", outbox.getId(), e.getMessage(), e);
        }
    }

    private String mapEventToTopic(String eventType, String aggregateType) {
        switch (eventType) {
            case "OrderCreated":
            case "OrderCancelled":
                return "order-events";
            case "InventoryReserved":
            case "InventoryFailed":
            case "InventoryReleased":
                return "inventory-events";
            case "PaymentSuccess":
            case "PaymentFailed":
                return "payment-events";
            default:
                return aggregateType + "-events";
        }
    }
}