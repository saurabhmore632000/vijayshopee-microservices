package com.vijayshopee.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitResponse {
    private Long id;
    private String name;
    private String description;
}
