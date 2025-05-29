package com.example.Account.mapper;

import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.entities.Customer;
import com.example.Account.entities.Accounts;

public class CustomerDetailsMapper {

    public static CustomerDetailsDto mapToCustomerDetailsDto(Customer customer, Accounts accounts) {
//        if (customer == null || accounts == null) {
//            throw new IllegalArgumentException("Customer and Accounts must not be null");
//        }

        CustomerDetailsDto dto = new CustomerDetailsDto();
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setAccountNumber(accounts.getAccountNumber());
        dto.setAccountType(accounts.getAccountType());
        dto.setBranchAddress(accounts.getBranchAddress());

        return dto;
    }
}

