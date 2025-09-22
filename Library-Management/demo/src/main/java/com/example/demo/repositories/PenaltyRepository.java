package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Panalty;

@Repository
public interface PenaltyRepository extends JpaRepository<Panalty, String> {

    @Query("SELECT p FROM Panalty p WHERE p.user.userId = :userId")
    List<Panalty> getPenaltiesByUserId(@Param("userId") String userId);
}
