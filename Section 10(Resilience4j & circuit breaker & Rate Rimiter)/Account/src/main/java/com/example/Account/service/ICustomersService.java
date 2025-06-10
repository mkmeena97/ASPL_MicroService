package com.example.Account.service;

import com.example.Account.dto.CustomerDetailDto;

public interface ICustomersService {

    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    CustomerDetailDto fetchCustomerDetails( String correlationId, String mobileNumber);
}
