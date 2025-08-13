package com.example.orchestrator_service.kafka;

import com.example.common_events.dto.InventoryEvent;
import com.example.common_events.dto.PaymentEvent;
import com.example.orchestrator_service.service.SagaOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaEventListener {

    private final SagaOrchestratorService sagaService;

    @KafkaListener(topics = "inventory-events", groupId = "saga-group")
    public void handleInventoryEvent(InventoryEvent event) {
        sagaService.handleInventoryResponse(event);
    }

    @KafkaListener(topics = "payment-events", groupId = "saga-group")
    public void handlePaymentEvent(PaymentEvent event) {
        sagaService.handlePaymentResponse(event);
    }
}
