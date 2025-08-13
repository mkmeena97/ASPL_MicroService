package com.example.demo.repository;

import com.example.common_events.Enums.InventoryStatus;
import com.example.demo.entity.InventoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, String> {
    List<InventoryRequest> findByStatus(InventoryStatus status);
}

