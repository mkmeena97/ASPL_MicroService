package com.example.Account.repository;

import com.example.Account.entities.Transaction;
import com.example.Account.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Find all transactions for a specific account, ordered by date descending
    List<Transaction> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);

    // Fetch limited recent transactions for mini statement
    List<Transaction> findTop10ByAccountNumberOrderByCreatedAtDesc(String accountNumber);

    // Custom: find by type (CREDIT/DEBIT) if needed
    List<Transaction> findByAccountNumberAndTypeOrderByCreatedAtDesc(String accountNumber, String type);

    // If you use enums for type:
     List<Transaction> findByAccountNumberAndTypeOrderByCreatedAtDesc(String accountNumber, TransactionType type);
}

