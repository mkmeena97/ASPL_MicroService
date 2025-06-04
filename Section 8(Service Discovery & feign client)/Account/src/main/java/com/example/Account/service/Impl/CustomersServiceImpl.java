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
import com.example.Account.service.client.CardsFeignClient;
import com.example.Account.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailDto fetchCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailDto customerDetailDto = CustomerMapper.mapToCustomerDetailDto(customer, new CustomerDetailDto());
        customerDetailDto.setAccountsDto(AccountMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(mobileNumber);
        customerDetailDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);
        customerDetailDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailDto;

    }
}
