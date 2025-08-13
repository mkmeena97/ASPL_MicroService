package com.example.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication(scanBasePackages = {
		"com.example.order_service",
		"com.example.common_events"
})
@EnableJpaRepositories(basePackages = {
		"com.example.order_service.repository",
		"com.example.common_events.repository"
})
@EntityScan(basePackages = {
		"com.example.order_service.entity",
		"com.example.common_events.entity"
})
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
