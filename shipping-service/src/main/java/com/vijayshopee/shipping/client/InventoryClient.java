package com.vijayshopee.shipping.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "http://localhost:8087/api/inventory")
public interface InventoryClient {

    @PutMapping("/deduct")
    void deductStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );
}