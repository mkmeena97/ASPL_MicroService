package com.example.Account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Integer transactionId;
    private String accountNumber;
    private String type;               // "CREDIT" or "DEBIT" - can use enum if you wish
    private BigDecimal amount;
    private String description;
    private String relatedAccount;    // For transfer: the other party, if needed
    private LocalDateTime createdAt;

    // Optionally, you might include extra info (customer name, etc.) for richer history
}

