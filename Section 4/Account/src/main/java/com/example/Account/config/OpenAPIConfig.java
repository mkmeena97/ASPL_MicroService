package com.example.Account.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Accounts microservice REST API Documentation",
                description = "MyBank Accounts microservice REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Mahendra kumar",
                        email = "developer@audilitycssolutions.com",
                        url = "https://www.audilitycssolutions.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.audilitycssolutions.com"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "MyBank Accounts microservice REST API Documentation",
                url = "http://localhost:8080/swagger-ui.html"
        )
)
public class OpenAPIConfig {
}

