package com.example.gatewayserver.controller;

import com.example.gatewayserver.dto.*;
import com.example.gatewayserver.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication and User Account Controller.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registration successful.");
    }

    /**
     * Login and retrieve access JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        Map<String, Object> tokens = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }

    /**
     * Fetch user profile.
     * Typically, the username is pulled from the authenticated principal,
     * but for illustration, it is supplied as a query param or path variable.
     */
    @GetMapping("/profile/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String username) {
        ProfileResponse profile = authService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update user profile.
     */
    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody ProfileUpdateRequest request) {
        authService.updateProfile(username, request);
        return ResponseEntity.ok("Profile updated successfully.");
    }

    /**
     * Trigger password reset email.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> triggerPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.triggerPasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset email (if supported) triggered.");
    }
}
