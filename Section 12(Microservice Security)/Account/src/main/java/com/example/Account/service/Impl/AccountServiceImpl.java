package com.example.Account.service.Impl;

import com.example.Account.constants.AccountsConstants;
import com.example.Account.dto.*;
import com.example.Account.entities.Accounts;
import com.example.Account.entities.Customer;
import com.example.Account.entities.Transaction;
import com.example.Account.enums.AccountStatus;
import com.example.Account.enums.TransactionType;
import com.example.Account.exception.CustomerAlreadyExistsException;
import com.example.Account.exception.ResourceNotFoundException;
import com.example.Account.mapper.AccountMapper;
import com.example.Account.mapper.CustomerDetailsMapper;
import com.example.Account.mapper.CustomerMapper;
import com.example.Account.mapper.TransactionMapper;
import com.example.Account.repository.AccountsRepository;
import com.example.Account.repository.CustomerRepository;
import com.example.Account.repository.TransactionRepository;
import com.example.Account.service.IAccountsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountsService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    // 1. Account creation
    @Override
    public void createAccount(CustomerDto customerDto) {
        if (customerRepository.findByMobileNumber(customerDto.getMobileNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already exists with mobile " + customerDto.getMobileNumber());
        }

        Customer customer = CustomerMapper.toCustomer(customerDto);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy("SYSTEM");
        Customer savedCustomer = customerRepository.save(customer);

        Accounts account = new Accounts();
        account.setCustomerId(savedCustomer.getCustomerId());
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(customerDto.getAccountType());
        account.setBranchAddress(customerDto.getBranchAddress());
        account.setStatus(AccountStatus.valueOf(AccountStatus.ACTIVE.name()));
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());
        account.setCreatedBy("SYSTEM");
        Accounts savedAccount = accountsRepository.save(account);

        // No initial transaction; balance starts as zero.
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%012d", (long) (Math.random() * 1_000_000_000_000L));
        } while (accountsRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }


    // 2. Add Money
    @Override
    @Transactional
    public void addMoney(AddMoneyRequestDto requestDto) {
        Customer customer = customerRepository.findByMobileNumber(requestDto.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", requestDto.getMobileNumber()));

        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        if (AccountStatus.FROZEN.name().equalsIgnoreCase(String.valueOf(account.getStatus()))) {
            throw new IllegalStateException("Account is frozen. No transactions allowed.");
        }

        account.setBalance(account.getBalance().add(requestDto.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("SYSTEM");
        accountsRepository.save(account);

        // Record transaction
        Transaction tx = new Transaction();
        tx.setAccountNumber(account.getAccountNumber());
        tx.setType(TransactionType.valueOf(TransactionType.CREDIT.name()));
        tx.setAmount(requestDto.getAmount());
        tx.setDescription("Deposit");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    // 3. Transfer Money
    @Override
    @Transactional
    public void transferMoney(TransferMoneyRequestDto requestDto) {
        if (requestDto.getFromMobileNumber().equals(requestDto.getToMobileNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Source customer/account
        Customer fromCustomer = customerRepository.findByMobileNumber(requestDto.getFromMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", requestDto.getFromMobileNumber()));
        Accounts fromAccount = accountsRepository.findByCustomerId(fromCustomer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", fromCustomer.getCustomerId().toString()));

        // Destination customer/account
        Customer toCustomer = customerRepository.findByMobileNumber(requestDto.getToMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", requestDto.getToMobileNumber()));
        Accounts toAccount = accountsRepository.findByCustomerId(toCustomer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", toCustomer.getCustomerId().toString()));

        if (AccountStatus.FROZEN.name().equalsIgnoreCase(String.valueOf(fromAccount.getStatus()))) {
            throw new IllegalStateException("Sender's account is frozen. No transactions allowed.");
        }
        if (AccountStatus.FROZEN.name().equalsIgnoreCase(String.valueOf(toAccount.getStatus()))) {
            throw new IllegalStateException("Recipient's account is frozen. Cannot accept transfer.");
        }
        if (fromAccount.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds in sender's account");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(requestDto.getAmount()));
        fromAccount.setUpdatedAt(LocalDateTime.now());
        fromAccount.setUpdatedBy("SYSTEM");
        toAccount.setBalance(toAccount.getBalance().add(requestDto.getAmount()));
        toAccount.setUpdatedAt(LocalDateTime.now());
        toAccount.setUpdatedBy("SYSTEM");

        accountsRepository.save(fromAccount);
        accountsRepository.save(toAccount);

        // Record debit transaction (sender)
        Transaction debitTx = new Transaction();
        debitTx.setAccountNumber(fromAccount.getAccountNumber());
        debitTx.setType(TransactionType.valueOf(TransactionType.DEBIT.name()));
        debitTx.setAmount(requestDto.getAmount());
        debitTx.setDescription("Transfer to " + requestDto.getToMobileNumber());
        debitTx.setRelatedAccount(toAccount.getAccountNumber());
        debitTx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(debitTx);

        // Record credit transaction (recipient)
        Transaction creditTx = new Transaction();
        creditTx.setAccountNumber(toAccount.getAccountNumber());
        creditTx.setType(TransactionType.valueOf(TransactionType.CREDIT.name()));
        creditTx.setAmount(requestDto.getAmount());
        creditTx.setDescription("Transfer from " + requestDto.getFromMobileNumber());
        creditTx.setRelatedAccount(fromAccount.getAccountNumber());
        creditTx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(creditTx);
    }

    // 4. Get Account Balance
    @Override
    public AccountBalanceDto getBalance(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        return AccountMapper.toAccountBalanceDto(account, customer.getMobileNumber());
    }

    // 5. Fetch Account (full details)
    @Override
    public CustomerDetailsDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        return CustomerDetailsMapper.mapToCustomerDetailsDto(customer, account);
    }

    // 6. Update Account Details (and/or customer details)
    @Override
    @Transactional
    public boolean updateAccount(CustomerDetailsDto dto) {
        if (dto.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account number must not be null");
        }
        Accounts account = accountsRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", dto.getAccountNumber().toString()));
        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", account.getCustomerId().toString()));

        CustomerMapper.updateCustomerFromDetailsDto(dto, customer);
        AccountMapper.updateAccountsFromDto(dto, account);

        customer.setUpdatedAt(LocalDateTime.now());
        customer.setUpdatedBy("SYSTEM");
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("SYSTEM");

        customerRepository.save(customer);
        accountsRepository.save(account);

        return true;
    }

    // 7. Delete account (and customer)
    @Override
    @Transactional
    public boolean deleteAccount(String mobileNumber) {
        Optional<Customer> customerOpt = customerRepository.findByMobileNumber(mobileNumber);
        if (customerOpt.isEmpty()) return false;
        Customer customer = customerOpt.get();
        Optional<Accounts> accountOpt = accountsRepository.findByCustomerId(customer.getCustomerId());
        accountOpt.ifPresent(account -> accountsRepository.deleteById(account.getAccountNumber()));
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    // 8. Freeze/Unfreeze
    @Override
    @Transactional
    public void freezeOrUnfreeze(FreezeUnfreezeRequestDto request) {
        Customer customer = customerRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", request.getMobileNumber()));
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        String newStatus = switch (request.getAction()) {
            case FREEZE -> AccountStatus.FROZEN.name();
            case UNFREEZE -> AccountStatus.ACTIVE.name();
        };
        account.setStatus(AccountStatus.valueOf(newStatus));
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("SYSTEM");
        accountsRepository.save(account);
    }

    // 9. Change Account Type
    @Override
    @Transactional
    public void changeAccountType(ChangeAccountTypeRequestDto request) {
        Customer customer = customerRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", request.getMobileNumber()));
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        account.setAccountType(request.getNewAccountType());
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("SYSTEM");
        accountsRepository.save(account);
    }
}
