package com.vijayshopee.shipping.service;

import com.vijayshopee.shipping.dto.ShippingResponse;

public interface ShippingService {
    ShippingResponse getShippingByOrderId(Long orderId);

    ShippingResponse shipOrder(Long orderId, Long productId, Integer quantity);
}
