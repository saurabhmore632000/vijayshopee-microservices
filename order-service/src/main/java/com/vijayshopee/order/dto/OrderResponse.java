package com.vijayshopee.order.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
//    private Long userId;
    private String email;
    private Double totalAmount;
    private String status;
    private LocalDateTime orderDate;

    private String customerName;
    private String shippingAddress;
    private String mobileNumber;
}