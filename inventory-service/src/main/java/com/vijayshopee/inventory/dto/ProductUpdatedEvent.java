package com.vijayshopee.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdatedEvent {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Long quantity;
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
}
