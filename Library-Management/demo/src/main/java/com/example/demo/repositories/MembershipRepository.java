package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Membership;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {

}
