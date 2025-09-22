package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Membership;
import com.example.demo.entities.Users;
import com.example.demo.repositories.MembershipRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class MembershipService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MembershipRepository membershipRepository;
	
	public ResponseEntity<?> checkMembership(String userId) {


        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        Users user = userOpt.get();

        Membership membership = user.getMembership();
        if (membership == null) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User does not have a membership");
        }

        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(membership.getStartDate()) || currentDate.isAfter(membership.getEndDate())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Membership is not active");
        }

        return ResponseEntity.ok(membership);
    }

	public ResponseEntity<Membership> addmembership(Membership membership) {
		
		Membership savedMembership = membershipRepository.save(membership);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(savedMembership);
	}

	public ResponseEntity<List<Membership>> getAllMemberships() {
		List<Membership> plans = membershipRepository.findAll();
		return ResponseEntity.ok(plans);
	}
	
    public ResponseEntity<String> assignMembershipToUser(String userId, String membershipId) {
    	
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Membership membership = membershipRepository.findById(membershipId).orElse(null);
        if (membership == null) {
            return ResponseEntity.status(404).body("Membership plan not found");
        }

        user.setMembership(membership);

        userRepository.save(user);

        return ResponseEntity.status(200).body("Membership successfully assigned to the user");
    }
}
