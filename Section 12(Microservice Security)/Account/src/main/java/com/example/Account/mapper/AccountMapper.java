package com.example.Account.mapper;

import com.example.Account.dto.AccountBalanceDto;
import com.example.Account.dto.AccountsDto;
import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.entities.Accounts;
import com.example.Account.enums.AccountStatus;

public class AccountMapper {

    /**
     * Maps Accounts entity to CustomerDetailsDto.
     */
    public static CustomerDetailsDto toCustomerDetailsDto(Accounts account,
                                                          String name,
                                                          String email,
                                                          String mobileNumber) {
        CustomerDetailsDto dto = new CustomerDetailsDto();
        dto.setCustomerId(account.getCustomerId());
        dto.setName(name);
        dto.setEmail(email);
        dto.setMobileNumber(mobileNumber);

        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBranchAddress(account.getBranchAddress());
        dto.setBalance(account.getBalance());
        dto.setStatus(String.valueOf(account.getStatus()));
        return dto;
    }

    /**
     * Maps CustomerDetailsDto to Accounts entity (for update operations).
     */
    public static void updateAccountsFromDto(CustomerDetailsDto dto, Accounts account) {
        if (dto.getAccountType() != null) {
            account.setAccountType(dto.getAccountType());
        }
        if (dto.getBranchAddress() != null) {
            account.setBranchAddress(dto.getBranchAddress());
        }
        if (dto.getBalance() != null) {
            account.setBalance(dto.getBalance());
        }
        if (dto.getStatus() != null) {
            account.setStatus(AccountStatus.valueOf(dto.getStatus()));
        }
        // Do not update accountNumber and customerId from here.
    }

    /**
     * Maps Accounts entity to AccountBalanceDto.
     */
    public static AccountBalanceDto toAccountBalanceDto(Accounts account, String mobileNumber) {
        AccountBalanceDto dto = new AccountBalanceDto();
        dto.setAccountNumber(account.getAccountNumber().toString());
        dto.setMobileNumber(mobileNumber);
        dto.setBalance(account.getBalance());
        dto.setAccountType(account.getAccountType());
        dto.setAccountStatus(String.valueOf(account.getStatus()));
        return dto;
    }

    public static Accounts mapToAccountsDto(AccountsDto accountsDto, Accounts accounts) {
        // Only update fields that are allowed to change.
        if (accountsDto.getAccountNumber() != null) {
            accounts.setAccountNumber(accountsDto.getAccountNumber());
        }
        if (accountsDto.getAccountType() != null) {
            accounts.setAccountType(accountsDto.getAccountType());
        }
        if (accountsDto.getBranchAddress() != null) {
            accounts.setBranchAddress(accountsDto.getBranchAddress());
        }
        if (accountsDto.getBalance() != null) {
            accounts.setBalance(accountsDto.getBalance());
        }
        if (accountsDto.getStatus() != null) {
            accounts.setStatus(AccountStatus.valueOf(accountsDto.getStatus()));
        }
        // add other fields as needed

        return accounts;
    }

}

