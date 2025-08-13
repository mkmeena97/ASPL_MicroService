package com.example.orderservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/fetch")
    public String placeOrder() {
        // Example: calling product-service via load-balanced WebClient
        String productResponse = webClientBuilder.build()
                .get()
                .uri("http://product-service/products/fetch")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Block only because this is not reactive endpoint

        return "Order Placed! Products fetched:\n" + productResponse;
    }
}
