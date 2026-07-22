package com.vijayshopee.order.client;

import com.vijayshopee.order.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/products")
public interface ProductClient {
    @GetMapping("/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);
}
