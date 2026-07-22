package com.vijayshopee.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingResponse {
    private Long id;
    private Long orderId;
    private String customerName;
    private String shippingAddress;
    private String mobileNumber;
    private String trackingNumber;
    private String shippingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}