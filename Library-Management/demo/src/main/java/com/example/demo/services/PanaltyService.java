package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Panalty;
import com.example.demo.repositories.PenaltyRepository;

@Service
public class PanaltyService {
	
	
	@Autowired
	private PenaltyRepository penaltyRepository;
	
	public ResponseEntity<Double> checkPenalties(String userId) {

        List<Panalty> penalties = penaltyRepository.getPenaltiesByUserId(userId);
        double totalPenaltyAmount = 0.0;

        for (Panalty penalty : penalties) {
            if (!penalty.isPaid()) {
                totalPenaltyAmount += penalty.getPenaltyAmount();
            }
        }

        if (totalPenaltyAmount == 0.0) {
            return ResponseEntity.status(HttpStatus.OK).body(0.0);  
        }

        return ResponseEntity.ok(totalPenaltyAmount);
    }

}
