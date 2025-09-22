package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Users;
import com.example.demo.services.UserService;




@RestController
@RequestMapping("user")
public class UserController {

	
		@Autowired
		private UserService userService;
		
	    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
		@PostMapping("/sign_up")
		public Users createUser(@RequestBody Users user) {
			user.setPassword(encoder.encode(user.getPassword()));
			return userService.createUser(user);
		}

		@PostMapping("/login")
		public Map<String, String> login(@RequestBody Users user) {
			return userService.verifyUser(user);
		}
	 	

	    @GetMapping("/")
	    public ResponseEntity<List<Users>> getAllUsers() {
	        return ResponseEntity.ok(userService.getAllUsers());
	    }


	    @GetMapping("/{userId}")
	    public ResponseEntity<Users> getUserById(@PathVariable String userId) {
	        return ResponseEntity.ok(userService.getUserById(userId));
	    }


	    @PutMapping("/{id}")
	    public ResponseEntity<Users> updateUser(@PathVariable String id, @RequestBody Users updatedUser) {
	        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
	    }

	    
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
	        userService.deleteUser(id);
	        return ResponseEntity.noContent().build();
	    }
}
