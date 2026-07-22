package com.vijayshopee.order.controller;

import com.vijayshopee.order.dto.OrderRequest;
import com.vijayshopee.order.dto.OrderResponse;
import com.vijayshopee.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest,
                                                    @RequestHeader("X-User-Email") String loggedInUserEmail,   // 🆔 Extracted by gateway from JWT
                                                    @RequestHeader("X-User-Role") String loggedInUserRole){

        if (loggedInUserRole == null || !loggedInUserRole.equalsIgnoreCase("ROLE_CUSTOMER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied!");
        }


        OrderResponse res = orderService.placeOrder(orderRequest,loggedInUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        // 📡 Call your order service layer to update the DB status
        orderService.updateStatus(id, status);

        return ResponseEntity.ok("Order status updated successfully to: " + status);
    }
}
