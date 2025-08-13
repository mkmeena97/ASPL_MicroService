package com.example.Account.service.Impl;

import com.example.Account.dto.AccountsDto;
import com.example.Account.dto.CardsDto;
import com.example.Account.dto.CustomerDetailDto;
import com.example.Account.dto.LoansDto;
import com.example.Account.entities.Accounts;
import com.example.Account.entities.Customer;
import com.example.Account.exception.ResourceNotFoundException;
import com.example.Account.mapper.AccountMapper;
import com.example.Account.mapper.CustomerMapper;
import com.example.Account.repository.AccountsRepository;
import com.example.Account.repository.CustomerRepository;
import com.example.Account.service.ICustomersService;
import com.example.Account.service.client.CardsFeignClient;
import com.example.Account.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final CardsFeignClient cardsFeignClient;
    private final LoansFeignClient loansFeignClient;

    /**
     * @param correlationId - For tracing, if needed.
     * @param mobileNumber - Input Mobile Number
     * @return CustomerDetailDto with all details
     */
    @Override
    public CustomerDetailDto fetchCustomerDetails(String correlationId, String mobileNumber) {

        // 1. Fetch customer by mobile
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        // 2. Fetch account by customerId
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        // 3. Map to CustomerDetailDto (assumes you have a DTO that combines all info in a nested way)
        CustomerDetailDto customerDetailDto = new CustomerDetailDto();

        // Set customer fields
        customerDetailDto.setCustomerId(customer.getCustomerId());
        customerDetailDto.setName(customer.getName());
        customerDetailDto.setEmail(customer.getEmail());
        customerDetailDto.setMobileNumber(customer.getMobileNumber());

        // Set account fields using mapper (adapt mapper or inline as needed)
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());
        accountsDto.setBalance(accounts.getBalance());
        accountsDto.setStatus(String.valueOf(accounts.getStatus()));
        customerDetailDto.setAccountsDto(accountsDto);

        // 4. Fetch and set loans info
        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (loansDtoResponseEntity != null && loansDtoResponseEntity.getBody() != null) {
            customerDetailDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        // 5. Fetch and set cards info
        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (cardsDtoResponseEntity != null && cardsDtoResponseEntity.getBody() != null) {
            customerDetailDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }

        return customerDetailDto;
    }
}
