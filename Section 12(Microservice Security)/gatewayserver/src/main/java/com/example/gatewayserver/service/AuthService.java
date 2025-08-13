package com.example.gatewayserver.service;

import com.example.gatewayserver.dto.*;
import com.example.gatewayserver.entity.User;
import com.example.gatewayserver.repository.UserRepository;
import com.example.gatewayserver.config.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user in Keycloak and the local database.
     * Aadhaar and PAN are optional.
     */
    @Transactional
    public void register(RegisterRequest request) {
        // Check if username or email already exist in DB
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Username already exists in DB"); });
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already exists in DB"); });

        // Also, you can (optionally) check for existence in Keycloak if needed.

        String keycloakId = keycloakService.createUserInKeycloak(request);

        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setAadhaarNumber(request.getAadhaarNumber());
        user.setPanNumber(request.getPanNumber());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddressLine1(request.getAddressLine1());
        user.setRole(request.getRole());
        userRepository.save(user); // persist to DB
    }

    /**
     * Login using Keycloak credentials, extract JWT, and return custom claims.
     */
    public LoginResponse login(LoginRequest request) {
        Map<String, Object> tokenResponse = keycloakService.loginAndGetTokens(request);

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");
        String idToken = (String) tokenResponse.get("id_token");

        String username = jwtUtil.extractUsername(accessToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in DB. Please register first."));

        return new LoginResponse(
                accessToken,
                refreshToken,
                idToken,
                user.getMobileNumber(),
                user.getFirstName(),
                user.getEmail(),
                user.getRole()
        );
    }


    /**
     * Refresh Token
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        return keycloakService.refreshToken(refreshToken);
    }


    /**
     * Fetch user profile by username.
     */
    public ProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getAadhaarNumber(),
                user.getPanNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getAddressLine1(),
                user.getRole()
        );
    }

    /**
     * Update user profile in both Keycloak and local DB.
     */
    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        keycloakService.updateUserInKeycloak(user.getKeycloakId(), request);

        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddressLine1(request.getAddressLine1());
        user.setAadhaarNumber(request.getAadhaarNumber());
        user.setPanNumber(request.getPanNumber());

        userRepository.save(user);
    }

    /**
     * Trigger password reset by email.
     */
    public void triggerPasswordReset(String email) {
        keycloakService.sendResetEmail(email);
    }
}
