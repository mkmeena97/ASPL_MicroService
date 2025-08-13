
package com.example.productservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/fetch")
    public String getProduct() {
        return "Product served from port: " + port;
    }
}
