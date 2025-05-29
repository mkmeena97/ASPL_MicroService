package com.example.Account.mapper;

import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.dto.CustomerDto;
import com.example.Account.entities.Customer;

public class CustomerMapper {

    public static CustomerDto mapToCustomerDto(Customer customer, CustomerDto customerDto){

        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setMobileNumber(customer.getMobileNumber());

        return customerDto;
    }

    public static Customer mapToCustomer(CustomerDto customerDto, Customer customer){

        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setMobileNumber(customerDto.getMobileNumber());

        return customer;
    }

    public static Customer mapToCustomerDetailsDto(CustomerDetailsDto customerDetailsDto, Customer customer){
        customer.setName(customerDetailsDto.getName());
        customer.setEmail(customerDetailsDto.getEmail());
        customer.setMobileNumber(customerDetailsDto.getMobileNumber());

        return customer;
    }
}
