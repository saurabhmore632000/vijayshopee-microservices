package com.vijayshopee.order.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String customerName;
    private String shippingAddress;
    private String mobileNumber;
}