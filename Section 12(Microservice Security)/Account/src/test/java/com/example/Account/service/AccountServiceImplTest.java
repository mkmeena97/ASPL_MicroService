package com.example.Account.service;

import com.example.Account.dto.AddMoneyRequestDto;
import com.example.Account.entities.Accounts;
import com.example.Account.entities.Customer;
import com.example.Account.enums.AccountStatus;
import com.example.Account.exception.ResourceNotFoundException;
import com.example.Account.repository.AccountsRepository;
import com.example.Account.repository.CustomerRepository;
import com.example.Account.repository.TransactionRepository;
import com.example.Account.service.Impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private AccountsRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private AccountServiceImpl accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountServiceImpl(accountRepository, customerRepository, transactionRepository);
    }

    @Test
    void addMoney_happyPath_updatesBalanceAndSavesTransaction() {
        // Arrange
        String mobile = "9876543210";
        BigDecimal amount = new BigDecimal("1000.00");
        AddMoneyRequestDto dto = new AddMoneyRequestDto(mobile, amount);

        Customer cust = new Customer();
        cust.setCustomerId(1);
        cust.setMobileNumber(mobile);

        Accounts acc = new Accounts();
        acc.setAccountNumber(42);
        acc.setCustomerId(1);
        acc.setBalance(new BigDecimal("500.00"));
        acc.setStatus(AccountStatus.valueOf(AccountStatus.ACTIVE.name()));

        when(customerRepository.findByMobileNumber(mobile)).thenReturn(Optional.of(cust));
        when(accountRepository.findByCustomerId(1)).thenReturn(Optional.of(acc));

        // Act
        accountService.addMoney(dto);

        // Assert
        assertEquals(new BigDecimal("1500.00"), acc.getBalance());
        verify(accountRepository).save(acc);
        verify(transactionRepository).save(any());
    }

    @Test
    void addMoney_accountNotFound_throwsException() {
        String mobile = "9999999999";
        AddMoneyRequestDto dto = new AddMoneyRequestDto(mobile, new BigDecimal("100.00"));
        when(customerRepository.findByMobileNumber(mobile)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accountService.addMoney(dto));
    }
}

