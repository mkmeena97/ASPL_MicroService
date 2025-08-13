package com.example.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import org.springframework.web.cors.CorsConfiguration;


import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
        @Bean
        public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
            serverHttpSecurity
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers(HttpMethod.GET).permitAll()
                                    .pathMatchers("/api/auth/**", "/h2-console/**").permitAll()
                            .pathMatchers("/aspl/accounts/**").hasRole("USER")
                            .pathMatchers("/aspl/cards/**").hasRole("USER")
                            .pathMatchers("/aspl/loans/**").hasRole("USER")
//                            .anyExchange().denyAll()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                    )
                    .csrf(ServerHttpSecurity.CsrfSpec::disable);


            return serverHttpSecurity.build();
        }

        private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){
            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
            return  new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
        }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control",
                "*"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
