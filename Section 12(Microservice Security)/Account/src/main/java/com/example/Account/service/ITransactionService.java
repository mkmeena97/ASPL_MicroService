package com.example.Account.service;

import com.example.Account.dto.TransactionDto;

import java.util.List;

public interface ITransactionService {

    /**
     * Returns a list of the most recent transactions for the given mobile number.
     *
     * @param mobileNumber the account holder's mobile number
     * @param limit how many transactions to return (e.g. 10 for mini statement)
     * @return list of TransactionDto objects sorted newest first
     */
    List<TransactionDto> getMiniStatement(String mobileNumber, int limit);

    // You can add more complex history or filtering methods if needed.
    // e.g. transaction history for a date range, filter by type, etc.
}

