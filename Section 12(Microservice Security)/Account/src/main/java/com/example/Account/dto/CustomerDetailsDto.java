package com.example.Account.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsDto {

    // Customer info
    private Integer customerId;
    private String name;
    private String email;
    private String mobileNumber;

    // Account info
    private String accountNumber;
    private String accountType;
    private String branchAddress;
    private BigDecimal balance;
    private String status; // or use AccountStatus enum

    // Optionally:
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
}

