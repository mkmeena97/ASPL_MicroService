package com.example.demo.controller;

import com.example.common_events.Enums.InventoryStatus;
import com.example.common_events.dto.InventoryEvent;
import com.example.common_events.entity.Outbox;
import com.example.common_events.dto.OutboxStatus;
import com.example.common_events.repository.OutboxRepository;
import com.example.demo.entity.InventoryRequest;
import com.example.demo.kafka.OutboxPublisher;
import com.example.demo.repository.InventoryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryRequestRepository inventoryRequestRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final OutboxPublisher outboxPublisher;

    @GetMapping("/pending")
    public List<InventoryRequest> getPendingRequests() {
        return inventoryRequestRepository.findByStatus(InventoryStatus.PENDING);
    }

    @PostMapping("/{orderId}/accept")
    @Transactional
    public ResponseEntity<String> acceptInventory(@PathVariable String orderId) {
        return inventoryRequestRepository.findById(orderId)
                .map(request -> {
                    request.setStatus(InventoryStatus.READY_FOR_PAYMENT);
                    inventoryRequestRepository.save(request);

                    // prepare InventoryEvent
                    InventoryEvent event = InventoryEvent.builder()
                            .eventType("InventoryEvent")
                            .orderId(orderId)
                            .product(request.getProduct())
                            .quantity(request.getQuantity())
                            .status(InventoryStatus.READY_FOR_PAYMENT)
                            .build();

                    Outbox outbox = Outbox.builder()
                            .id(UUID.randomUUID().toString())
                            .aggregateType("inventory")
                            .eventType("InventoryReserved")
                            .payload(serialize(event))
                            .status(OutboxStatus.PENDING)
                            .attemptCount(0)
                            .build();

                    outbox = outboxRepository.save(outbox);
                    outboxPublisher.publishSingle(outbox);

                    return ResponseEntity.ok("Inventory accepted and event queued.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/reject")
    @Transactional
    public ResponseEntity<String> rejectInventory(@PathVariable String orderId) {
        return inventoryRequestRepository.findById(orderId)
                .map(request -> {
                    request.setStatus(InventoryStatus.INVENTORY_FAILED);
                    inventoryRequestRepository.save(request);

                    InventoryEvent event = InventoryEvent.builder()
                            .eventType("InventoryEvent")
                            .orderId(orderId)
                            .product(request.getProduct())
                            .quantity(request.getQuantity())
                            .status(InventoryStatus.INVENTORY_FAILED)
                            .build();

                    Outbox outbox = Outbox.builder()
                            .id(UUID.randomUUID().toString())
                            .aggregateType("inventory")
                            .eventType("InventoryFailed")
                            .payload(serialize(event))
                            .status(OutboxStatus.PENDING)
                            .attemptCount(0)
                            .build();

                    outbox = outboxRepository.save(outbox);
                    outboxPublisher.publishSingle(outbox);

                    return ResponseEntity.ok("Inventory rejected and event queued.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String serialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
