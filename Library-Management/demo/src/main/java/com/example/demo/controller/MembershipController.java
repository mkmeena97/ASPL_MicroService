package com.example.demo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Membership;
import com.example.demo.services.MembershipService;

@RestController
@RequestMapping("/membership")
public class MembershipController {
	
		
		@Autowired
		private MembershipService membershipService;
	
	    @GetMapping("/checkMembership/{userId}")
	    public ResponseEntity<?> checkMembership(@PathVariable String userId) {
	        return membershipService.checkMembership(userId);
	    }
	    
	    @GetMapping("/getAllPlans")
	    public ResponseEntity<List<Membership>> getAllMembershipPlans(){
	    	return membershipService.getAllMemberships();
	    }
	    
	    @PostMapping("/admin/addMembership")
	    public ResponseEntity<Membership> addMembershipDetails(@RequestBody Membership membership){
	    	return membershipService.addmembership(membership);
	    }
	    
	    @PostMapping("/assignMembership")
	    public ResponseEntity<String> assignMembershipToUser(
	        @RequestParam String userId,
	        @RequestParam String membershipId) {
	        
	        return membershipService.assignMembershipToUser(userId, membershipId);
	    }
}
