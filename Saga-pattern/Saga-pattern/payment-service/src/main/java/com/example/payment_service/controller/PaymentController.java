package com.example.payment_service.controller;

import com.example.common_events.Enums.PaymentStatus;
import com.example.payment_service.entity.PaymentRequest;
import com.example.payment_service.repository.PaymentRequestRepository;
import com.example.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentService paymentService;

    @GetMapping("/pending")
    public List<PaymentRequest> getPendingPayments() {
        return paymentRequestRepository.findByStatus(PaymentStatus.PENDING);
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmPayment(@PathVariable String orderId) {
        return paymentRequestRepository.findById(orderId).map(req -> {
            if (req.getStatus() != PaymentStatus.PENDING) {
                return ResponseEntity.status(409).body("Payment already processed or not in a confirmable state.");
            }
            boolean ok = paymentService.confirmPayment(orderId);
            return ok ? ResponseEntity.ok("Payment confirmed and event queued.")
                    : ResponseEntity.status(500).body("Failed to update payment status.");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/reject")
    public ResponseEntity<String> rejectPayment(@PathVariable String orderId) {
        return paymentRequestRepository.findById(orderId).map(req -> {
            if (req.getStatus() != PaymentStatus.PENDING) {
                return ResponseEntity.status(409).body("Payment already processed or not in a rejectable state.");
            }
            boolean ok = paymentService.rejectPayment(orderId);
            return ok ? ResponseEntity.ok("Payment rejected and event queued.")
                    : ResponseEntity.status(500).body("Failed to update payment status.");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
