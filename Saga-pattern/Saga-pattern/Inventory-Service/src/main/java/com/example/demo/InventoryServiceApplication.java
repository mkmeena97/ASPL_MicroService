package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication(scanBasePackages = {
		"com.example.demo",
		"com.example.common_events"
})
@EnableJpaRepositories(basePackages = {
		"com.example.demo.repository",
		"com.example.common_events.repository"
})
@EntityScan(basePackages = {
		"com.example.demo.entity",
		"com.example.common_events.entity"
})
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
