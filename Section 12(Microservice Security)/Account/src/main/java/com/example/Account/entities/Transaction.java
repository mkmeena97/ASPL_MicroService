package com.example.Account.entities;

import com.example.Account.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transaction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "related_account")
    private String relatedAccount; // for transfer: other account's number

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

//     Optionally, you can add reference if you want (not required for basic)
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "account_number", insertable = false, updatable = false)
     private Accounts account;
}

