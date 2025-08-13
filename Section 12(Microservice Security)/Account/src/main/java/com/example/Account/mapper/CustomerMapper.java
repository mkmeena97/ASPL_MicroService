package com.example.Account.mapper;

import com.example.Account.dto.CustomerDetailDto;
import com.example.Account.dto.CustomerDto;
import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.entities.Customer;

public class CustomerMapper {

    // Map CustomerDto to Customer entity (for account creation)
    public static Customer toCustomer(CustomerDto dto) {
        Customer entity = new Customer();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setMobileNumber(dto.getMobileNumber());
        // createdAt/createdBy/audit fields handled elsewhere (e.g. in service or JPA auditing)
        return entity;
    }

    // Map Customer entity to CustomerDetailsDto (used in fetch API)
    public static CustomerDetailsDto toCustomerDetailsDto(Customer customer) {
        CustomerDetailsDto dto = new CustomerDetailsDto();
        dto.setCustomerId(customer.getCustomerId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setMobileNumber(customer.getMobileNumber());
        // Account fields to be set in AccountMapper or service
        return dto;
    }

    // Update existing Customer entity from CustomerDetailsDto (for update operation)
    public static void updateCustomerFromDetailsDto(CustomerDetailsDto dto, Customer entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getMobileNumber() != null) {
            entity.setMobileNumber(dto.getMobileNumber());
        }
    }

    public static CustomerDetailDto mapToCustomerDetailDto(Customer customer, CustomerDetailDto customerDetailDto) {
        customerDetailDto.setName(customer.getName());
        customerDetailDto.setEmail(customer.getEmail());
        customerDetailDto.setMobileNumber(customer.getMobileNumber());
        return customerDetailDto;
    }
}
