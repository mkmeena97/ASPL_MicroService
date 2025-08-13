package com.example.Account.service.Impl;

import com.example.Account.dto.TransactionDto;
import com.example.Account.entities.Accounts;
import com.example.Account.entities.Customer;
import com.example.Account.entities.Transaction;
import com.example.Account.exception.ResourceNotFoundException;
import com.example.Account.mapper.TransactionMapper;
import com.example.Account.repository.AccountsRepository;
import com.example.Account.repository.CustomerRepository;
import com.example.Account.repository.TransactionRepository;
import com.example.Account.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final AccountsRepository accountsRepository;

    /**
     * Returns a list of the most recent transactions for the given mobile number.
     *
     * @param mobileNumber Mobile number identifying the account
     * @param limit        Maximum number of recent transactions to fetch
     * @return List of TransactionDto
     */
    @Override
    public List<TransactionDto> getMiniStatement(String mobileNumber, int limit) {
        // 1. Find customer by mobile number
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        // 2. Find account by customer ID
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        // 3. Fetch recent transactions on this account, ordered newest first
        List<Transaction> transactions = transactionRepository
                .findByAccountNumberOrderByCreatedAtDesc(account.getAccountNumber());

        // 4. Limit result
        List<Transaction> limitedList = transactions.stream()
                .limit(limit)
                .collect(Collectors.toList());

        // 5. Map to DTOs
        return limitedList.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }
}

