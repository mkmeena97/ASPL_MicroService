package com.example.Account.service;

import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.dto.CustomerDto;

public interface IAccountsService {

    /**
     *
     * @param customerDto- CustomerDto Object
     */
    void createAccount(CustomerDto customerDto);

    /**
     *
     * @param mobileNumber- input Mobile Number
     * @return
     */
    CustomerDetailsDto fetchAccount(String mobileNumber);

    /**
     *
     * @param customerDetailsDto
     * @return boolean indicating if the update of account details is successful or not
     */
    boolean updateAccount(CustomerDetailsDto customerDetailsDto);

    /**
     *
     * @param mobileNumber
     * @return boolean indicating if the deletion process of account details is successful or not
     */
    boolean deleteAccount(String mobileNumber);
}
