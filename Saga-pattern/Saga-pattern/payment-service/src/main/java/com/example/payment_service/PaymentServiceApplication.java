package com.example.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {
		"com.example.payment_service",
		"com.example.common_events"
})
@EnableJpaRepositories(basePackages = {
		"com.example.payment_service.repository",
		"com.example.common_events.repository"
})
@EntityScan(basePackages = {
		"com.example.payment_service.entity",
		"com.example.common_events.entity"
})
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
