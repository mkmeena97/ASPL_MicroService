package com.example.Account.service.Impl;

import com.example.Account.constants.AccountsConstants;
import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.dto.CustomerDto;
import com.example.Account.entities.Accounts;
import com.example.Account.entities.Customer;
import com.example.Account.exception.CustomerAlreadyExistsException;
import com.example.Account.exception.ResourceNotFoundException;
import com.example.Account.mapper.AccountMapper;
import com.example.Account.mapper.CustomerDetailsMapper;
import com.example.Account.mapper.CustomerMapper;
import com.example.Account.repository.AccountsRepository;
import com.example.Account.repository.CustomerRepository;
import com.example.Account.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountsService {

    private AccountsRepository accountRepository;
    private CustomerRepository customerRepository;

    /**
     * @param customerDto - CustomerDto Object
     */
    @Override
    public void createAccount(CustomerDto customerDto) {

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer =customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()){
            throw  new CustomerAlreadyExistsException("Customer already registered with given mobile number "
                    +customerDto.getMobileNumber());
        }

//        customer.setCreatedAt(LocalDateTime.now());     //no need of this after implemening auditing
//        customer.setCreatedBy("Anonymous");
        Customer savedCustomer = customerRepository.save(customer);

        accountRepository.save(createNewAccount(savedCustomer));
    }

    /**
     *
     * @param customer-Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer){
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());

        long rendomAccountNumber = 1000000000L+ new Random().nextInt(900000000);

        newAccount.setAccountNumber(rendomAccountNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }


    /**
     * @param mobileNumber - input Mobile Number
     * @return
     */
    @Override
    public CustomerDetailsDto fetchAccount(String mobileNumber) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("customer", "mobileNumber", mobileNumber)
        );

        Accounts account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()-> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerDetailsMapper.mapToCustomerDetailsDto(customer,account);
        return customerDetailsDto;
    }

    /**
     * @param customerDetailsDto
     * @return boolean indicating if the update of account details is successful or not
     */
    @Override
    public boolean updateAccount(CustomerDetailsDto customerDetailsDto) {
        boolean isUpdated = false;

        if(customerDetailsDto != null){
            Accounts accounts = accountRepository.findById(customerDetailsDto.getAccountNumber()).orElseThrow(
                    ()->new ResourceNotFoundException("Account", "AccountNumber", customerDetailsDto.getAccountNumber().toString())
            );
            AccountMapper.mapToCustomerDetailsDto(customerDetailsDto,accounts);
            accounts = accountRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    ()-> new ResourceNotFoundException("Customer", "CustomerId", customerId.toString())
            );
            CustomerMapper.mapToCustomerDetailsDto(customerDetailsDto,customer);
            customerRepository.save(customer);
            isUpdated=true;
        }

        return isUpdated;
    }

    /**
     * @param mobileNumber
     * @return boolean indicating if the deletion process of account details is successful or not
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("customer", "mobileNumber",mobileNumber)
        );
        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }


}
