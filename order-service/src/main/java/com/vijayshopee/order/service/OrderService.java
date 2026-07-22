package com.vijayshopee.order.service;

import com.vijayshopee.order.dto.OrderResponse;
import com.vijayshopee.order.dto.OrderRequest;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest, String email);

    void updateStatus(Long id, String status);
}
