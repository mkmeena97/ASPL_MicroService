package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.BorrowService;

@RestController
@RequestMapping("/borrow")
public class BorrowController {
	
	@Autowired
	private BorrowService borrowService;
	
	@PostMapping("/")
	public ResponseEntity<Map<String, Object>> borrowBook(@RequestParam String userId, @RequestParam String bookId) {
	    try {
	    
	        return borrowService.borrowBook(userId, bookId);
	    } catch (Exception e) {
	      
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "An error occurred: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}


}
