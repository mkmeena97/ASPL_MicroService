package com.example.payment_service.repository;


import com.example.common_events.Enums.PaymentStatus;
import com.example.payment_service.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, String> {
    List<PaymentRequest> findByStatus(PaymentStatus status);
}

