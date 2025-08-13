package com.example.gatewayserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class SecurityBeansConfig {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String issuerBase;

    /**
     * Configures the JwtDecoder bean to validate and decode JWT tokens signed by Keycloak.
     * Uses the realm's JWKS endpoint based on your application's configured realm and Keycloak server URL.
     */
    @Bean
    public JwtDecoder customJwtDecoder() {
        String issuerUri = issuerBase + "/realms/" + realm;
        return NimbusJwtDecoder.withJwkSetUri(issuerUri + "/protocol/openid-connect/certs").build();
    }
}
