package com.example.Account.repository;

import com.example.Account.entities.Accounts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;
import java.util.List;

public interface AccountsRepository extends JpaRepository<Accounts, String> {

    // Find by customer ID (for one-to-one or one-to-many mapping)
    Optional<Accounts> findByCustomerId(Integer customerId);

    // Find by mobile number via a join (assuming you have set up entity relationships accordingly)
    // If Customer and Accounts relationship is set up with @ManyToOne/@OneToOne, add a derived query:
    // Optional<Accounts> findByCustomer_MobileNumber(String mobileNumber);

    // Find by account number
    Optional<Accounts> findByAccountNumber(String accountNumber);

    // If you want all accounts for a given mobile (for multi-accounts per customer)
    List<Accounts> findAllByCustomerId(Integer customerId);

    @Transactional
    @Modifying
    void deleteByCustomerId(Long customerId);
}

