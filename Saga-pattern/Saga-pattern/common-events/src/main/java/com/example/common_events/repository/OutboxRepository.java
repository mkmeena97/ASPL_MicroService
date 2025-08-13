package com.example.common_events.repository;


import com.example.common_events.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, String> {

    // Find PENDING outbox entries (simple approach)
    @Query("SELECT o FROM Outbox o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<Outbox> findPending();

    // Optionally lock for processing (depends on DB and concurrency)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Outbox o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<Outbox> findPendingForProcessing();
}

