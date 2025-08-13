package com.example.orchestrator_service.controller;

import com.example.common_events.dto.OrderEvent;
import com.example.orchestrator_service.dto.OrderSagaState;
import com.example.orchestrator_service.service.SagaOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class SagaController {

    private final SagaOrchestratorService sagaService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startSaga(@RequestBody OrderEvent orderRequest) {
        sagaService.initiateOrder(orderRequest);
        return ResponseEntity.ok(Map.of("orderId", orderRequest.getOrderId(), "status", "STARTED"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderSagaState> getSaga(@PathVariable String orderId) {
        OrderSagaState s = sagaService.getSagaState(orderId);
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<OrderSagaState>> getAll() {
        return ResponseEntity.ok(sagaService.getAllStates());
    }
}
