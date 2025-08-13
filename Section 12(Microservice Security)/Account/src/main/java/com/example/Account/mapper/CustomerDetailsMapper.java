package com.example.Account.mapper;

import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.entities.Accounts;
import com.example.Account.entities.Customer;

public class CustomerDetailsMapper {

    /**
     * Combine Customer and Accounts entity fields into a CustomerDetailsDto.
     */
    public static CustomerDetailsDto mapToCustomerDetailsDto(Customer customer, Accounts account) {
        CustomerDetailsDto dto = new CustomerDetailsDto();

        // Customer information
        dto.setCustomerId(customer.getCustomerId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setMobileNumber(customer.getMobileNumber());

        // Account information
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBranchAddress(account.getBranchAddress());
        dto.setBalance(account.getBalance());
        dto.setStatus(String.valueOf(account.getStatus()));

        // Optionally, you could add audit fields here if present in DTO

        return dto;
    }
}
