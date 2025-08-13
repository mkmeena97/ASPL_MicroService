package com.example.Account.repository;

import com.example.Account.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Find by mobile number (for account management)
    Optional<Customer> findByMobileNumber(String mobileNumber);

    // Find by email (useful for user lookup)
    Optional<Customer> findByEmail(String email);

    // Optional: Delete by mobile number, if needed
    void deleteByMobileNumber(String mobileNumber);

    // More queries can be added as needed
}

