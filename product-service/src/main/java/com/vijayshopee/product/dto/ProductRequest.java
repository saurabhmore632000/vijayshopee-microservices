package com.vijayshopee.product.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Long categoryId;    // Selected Category ID
    private Long subCategoryId; // Selected SubCategory ID
    private Long quantity;
}