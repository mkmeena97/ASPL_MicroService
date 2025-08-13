package com.example.gatewayserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Autowired
    private JwtDecoder jwtDecoder;

    private Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    public String extractClaim(String token, String claim) {
        Jwt jwt = decode(token);
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : null;
    }

    public String extractUsername(String token) {
        return extractClaim(token, "preferred_username");
    }

    public String extractEmail(String token) {
        return extractClaim(token, "email");
    }

    public String extractMobile(String token) {
        return extractClaim(token, "mobileNumber");
    }

    public String extractFullName(String token) {
        String firstName = extractClaim(token, "given_name");
        String lastName = extractClaim(token, "family_name");
        StringBuilder fullName = new StringBuilder();
        if (firstName != null) fullName.append(firstName);
        if (lastName != null && !lastName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName);
        }
        return fullName.toString().trim();
    }
}
