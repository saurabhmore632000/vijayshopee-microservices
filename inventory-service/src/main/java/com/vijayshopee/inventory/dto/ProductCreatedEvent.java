package com.vijayshopee.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private Long productId;
    private String name;
    private Double price;
    private Integer initialQuantity;
}