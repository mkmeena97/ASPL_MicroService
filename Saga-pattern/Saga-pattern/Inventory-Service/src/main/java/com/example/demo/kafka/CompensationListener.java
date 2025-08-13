package com.example.demo.kafka;

import com.example.common_events.dto.InventoryReleaseEvent;
import com.example.common_events.dto.OrderCancelEvent;
import com.example.common_events.Enums.InventoryStatus;
import com.example.demo.entity.InventoryRequest;
import com.example.demo.repository.InventoryRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompensationListener {

    private final InventoryRequestRepository inventoryRequestRepository;
    private static final Logger log = LoggerFactory.getLogger(CompensationListener.class);

    @KafkaListener(topics = "inventory-release-events", groupId = "inventory-group")
    public void onInventoryRelease(InventoryReleaseEvent event) {
        Optional<InventoryRequest> opt = inventoryRequestRepository.findById(event.getOrderId());
        if (opt.isPresent()) {
            InventoryRequest req = opt.get();
            // Mark as released or remove; here we mark as PENDING/released logic — choose as per business.
            req.setStatus(InventoryStatus.PENDING); // or create a RELEASED enum if you have one
            inventoryRequestRepository.save(req);
            log.info(" Inventory released for order {}, product {}, qty {}", event.getOrderId(), event.getProduct(), event.getQuantity());
        } else {
            log.warn("InventoryReleaseEvent for unknown order {}", event.getOrderId());
        }
    }

    @KafkaListener(topics = "order-cancelled-events", groupId = "inventory-group")
    public void onOrderCancelled(OrderCancelEvent event) {
        Optional<InventoryRequest> opt = inventoryRequestRepository.findById(event.getOrderId());
        if (opt.isPresent()) {
            InventoryRequest req = opt.get();
            req.setStatus(InventoryStatus.INVENTORY_FAILED); // treat cancelled as failed/released
            inventoryRequestRepository.save(req);
            log.info(" Order cancelled: marking inventory failed for order {} (reason: {})", event.getOrderId(), event.getReason());
        } else {
            log.warn("OrderCancelEvent for unknown order {}", event.getOrderId());
        }
    }
}

