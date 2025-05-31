package com.example.Account.mapper;

import com.example.Account.dto.AccountsDto;
import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.entities.Accounts;

public class AccountMapper {

    public static AccountsDto mapToAccountsDto(Accounts accounts, AccountsDto accountsDto){
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());

        return accountsDto;
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts){

        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());

        return accounts;
    }

    public static  Accounts mapToCustomerDetailsDto(CustomerDetailsDto customerDetailsDto, Accounts accounts){
        accounts.setAccountNumber(customerDetailsDto.getAccountNumber());
        accounts.setAccountType(customerDetailsDto.getAccountType());
        accounts.setBranchAddress(customerDetailsDto.getBranchAddress());

        return accounts;
    }
}
