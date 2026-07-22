package com.vijayshopee.shipping.controller;

import com.vijayshopee.shipping.dto.ShippingResponse;
import com.vijayshopee.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping")
@CrossOrigin(origins = "http://localhost:5173")
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponse> getShippingByOrderId(@PathVariable Long orderId){
        ShippingResponse res = shippingService.getShippingByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/order/{orderId}/ship")
    public ResponseEntity<ShippingResponse> shipOrder(
            @PathVariable Long orderId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        ShippingResponse response = shippingService.shipOrder(orderId, productId, quantity);
        return ResponseEntity.ok(response);
    }
}
