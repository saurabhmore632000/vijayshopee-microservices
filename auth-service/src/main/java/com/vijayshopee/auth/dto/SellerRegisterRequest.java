package com.vijayshopee.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerRegisterRequest {
    private String email;
    private String password;
    private String shopName;
    private String shopDescription;
}