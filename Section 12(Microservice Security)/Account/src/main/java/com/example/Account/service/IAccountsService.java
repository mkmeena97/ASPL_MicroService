package com.example.Account.service;

import com.example.Account.dto.*;

public interface IAccountsService {
    /**
     * Create a new customer and their associated account.
     */
    void createAccount(CustomerDto customerDto);

    /**
     * Add money (deposit/top-up) to an account.
     */
    void addMoney(AddMoneyRequestDto requestDto);

    /**
     * Transfer money from one account to another.
     */
    void transferMoney(TransferMoneyRequestDto requestDto);

    /**
     * Get account balance by mobile number.
     */
    AccountBalanceDto getBalance(String mobileNumber);

    /**
     * Get account (and customer) details by mobile number.
     */
    CustomerDetailsDto fetchAccount(String mobileNumber);

    /**
     * Update customer/account details.
     */
    boolean updateAccount(CustomerDetailsDto dto);

    /**
     * Delete account (and customer record) by mobile number.
     */
    boolean deleteAccount(String mobileNumber);

    /**
     * Freeze or unfreeze an account.
     */
    void freezeOrUnfreeze(FreezeUnfreezeRequestDto request);

    /**
     * Change the type of an account.
     */
    void changeAccountType(ChangeAccountTypeRequestDto request);
}
