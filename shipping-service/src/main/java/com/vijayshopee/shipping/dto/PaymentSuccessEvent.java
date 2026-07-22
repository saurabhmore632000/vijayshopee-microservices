package com.vijayshopee.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {
    private Long orderId;
    private String customerName;
    private String shippingAddress;
    private String mobileNumber;
    private String paymentStatus; // e.g., "SUCCESS"
    private Double totalAmount;
}
