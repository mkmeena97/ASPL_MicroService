package com.example.Account.entities;


import com.example.Account.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Accounts extends BaseEntity {
    @Id
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType; // e.g. "SAVINGS", "CURRENT"

    @Column(name = "branch_address", nullable = false, length = 200)
    private String branchAddress;

    @Column(name = "balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status = AccountStatus.valueOf("ACTIVE"); // e.g. "ACTIVE", "FROZEN"

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    // private Customer customer;
}

