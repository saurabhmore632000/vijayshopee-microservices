package com.vijayshopee.product.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private Long productId;
    private String name;
    private Double price;
    private Integer initialQuantity; // We will pass a default configuration value (like 0)
}