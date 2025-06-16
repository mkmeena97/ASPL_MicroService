package com.example.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
        @Bean
        public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
            serverHttpSecurity
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers(HttpMethod.GET).permitAll()
                            .pathMatchers("/aspl/accounts/**").hasRole("ACCOUNTS")
                            .pathMatchers("/aspl/cards/**").hasRole("CARDS")
                            .pathMatchers("/aspl/loans/**").hasRole("LOANS")
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
}
