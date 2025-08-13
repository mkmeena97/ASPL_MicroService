package com.example.Account.mapper;

import com.example.Account.dto.TransactionDto;
import com.example.Account.entities.Transaction;
import com.example.Account.enums.TransactionType;

public class TransactionMapper {

    /**
     * Convert Transaction entity to TransactionDto
     */
    public static TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setAccountNumber(transaction.getAccountNumber());
        dto.setType(String.valueOf(transaction.getType()));
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setRelatedAccount(transaction.getRelatedAccount());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }

    /**
     * Convert TransactionDto to Transaction entity
     * (optional, only if you need it for some request handling)
     */
    public static Transaction toEntity(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setAccountNumber(dto.getAccountNumber());
        transaction.setType(TransactionType.valueOf(dto.getType()));
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setRelatedAccount(dto.getRelatedAccount());
        transaction.setCreatedAt(dto.getCreatedAt());
        return transaction;
    }
}

