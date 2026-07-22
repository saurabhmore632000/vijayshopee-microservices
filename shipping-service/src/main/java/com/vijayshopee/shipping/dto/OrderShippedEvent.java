package com.vijayshopee.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippedEvent {
    private Long orderId;
    private String shippingStatus; // will be "DISPATCHED"
    private String trackingNumber;
}