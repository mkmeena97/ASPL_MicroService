package com.example.order_service.repository;

import com.example.order_service.entity.OrderSagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSagaRepository extends JpaRepository<OrderSagaState, String> {
}

