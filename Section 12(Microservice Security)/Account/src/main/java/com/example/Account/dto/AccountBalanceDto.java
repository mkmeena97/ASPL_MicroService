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
public class AccountBalanceDto {
    private String accountNumber;   // Or Integer/Long, depending on your PK type
    private String mobileNumber;
    private BigDecimal balance;
    private String accountType;
    private String accountStatus;

    // Optional
    // private String currency;
}

