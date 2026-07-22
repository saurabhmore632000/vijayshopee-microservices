package com.vijayshopee.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {
    private Long orderId;
    private String customerName;
    private String shippingAddress;
    private String mobileNumber;
    private String paymentStatus;
    private Double totalAmount;
}