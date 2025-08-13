package com.example.order_service.controller;

import com.example.common_events.Enums.OrderStatus;
import com.example.common_events.dto.OrderEvent;
import com.example.order_service.entity.OrderSagaState;
import com.example.order_service.repository.OrderSagaRepository;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderSagaRepository orderSagaRepository;

    @PostMapping("/place")
    @Transactional
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody OrderEvent orderRequest) {
        String orderId = UUID.randomUUID().toString();
        orderRequest.setOrderId(orderId);
        orderRequest.setStatus(OrderStatus.PENDING);
        orderService.publishOrder(orderRequest);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "status", OrderStatus.PENDING.name()

        ));
    }

    @GetMapping("/trace/{orderId}")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable String orderId) {
        return orderSagaRepository.findById(orderId)
                .map(order -> ResponseEntity.ok(Map.of(
                        "orderId", orderId,
                        "status", order.getOrderStatus() != null
                                ? order.getOrderStatus().name()
                                : "NOT_FOUND"
                )))
                .orElse(ResponseEntity.ok(Map.of(
                        "orderId", orderId,
                        "status", "NOT_FOUND"
                )));
    }

    @GetMapping("/trace")
    public ResponseEntity<Map<String, String>> getAllOrders() {
        Map<String, String> result = orderSagaRepository.findAll().stream()
                .collect(Collectors.toMap(
                        OrderSagaState::getOrderId,
                        e -> e.getOrderStatus() != null
                                ? e.getOrderStatus().name()
                                : "NOT_FOUND"
                ));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderSagaState> getSagaState(@PathVariable String orderId) {
        return orderSagaRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public List<OrderSagaState> getAllOrdersSagaState() {
        return orderSagaRepository.findAll();
    }
}
