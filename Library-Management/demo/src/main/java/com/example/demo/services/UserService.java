package com.example.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Users;
import com.example.demo.exceptions.UserCreationException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repositories.UserRepository;



@Service
public class UserService {
	
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepo;
	
    @Autowired
    AuthenticationManager authManager;
    
    @Autowired
    private JWTService jwtService;
	
	public Users createUser(Users user) {
		log.info("Creating user: {}", user.getUsername());
		log.debug("User details: {}", user);
		
		try {
			
			Users savedUser = userRepo.save(user);
			log.debug("User successfully created with ID: {}", savedUser.getUserId());
			return savedUser;
			
		}catch(Exception e) {
			log.error("Error occurred while creating user: {}", e.getMessage(), e);
			
			throw new UserCreationException("Failed to create user: " + user.getUsername(), e);
		}
	}

	public List<Users> getAllUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
	}

	public Users getUserById(String userId) {
        log.info("Fetching user with Id: {}", userId);
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

	}

	public Users updateUser(String id, Users updatedUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteUser(String id) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public Map<String, String> verifyUser(Users user) {
		
	    // Determine if the user provided a username, email, or phone number
	    String credential = user.getUsername();

	    // Check for phone number or email
	    if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
	        credential = user.getPhoneNumber();
	    } else if (user.getEmail() != null && !user.getEmail().isEmpty()) {
	        credential = user.getEmail();
	    }
	    
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(credential, user.getPassword()));
	    if (authentication.isAuthenticated()) {
	        Users fullUser = userRepo.findByUsernameOrEmailOrPhoneNumber(credential, credential, credential)
                    .orElseThrow(() -> new RuntimeException("User not found"));

	        // Generate access and refresh tokens
	        String accessToken = jwtService.generateAccessToken(fullUser);
	        String refreshToken = jwtService.generateRefreshToken(fullUser);

	        // Return tokens in a response map
	        Map<String, String> tokens = new HashMap<>();
	        tokens.put("accessToken", accessToken);
	        tokens.put("refreshToken", refreshToken);
	        return tokens;
	    }
		
	    throw new RuntimeException("Login Failed");
	}
	
}
