package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Users;



@Repository
public interface UserRepository extends JpaRepository<Users, String>{

	Optional<Users> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);
}
