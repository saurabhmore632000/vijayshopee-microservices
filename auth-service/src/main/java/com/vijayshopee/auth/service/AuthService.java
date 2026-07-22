package com.vijayshopee.auth.service;


import com.vijayshopee.auth.dto.AuthRequest;
import com.vijayshopee.auth.dto.AuthResponse;
import com.vijayshopee.auth.dto.SellerRegisterRequest;

public interface AuthService {

    // Contract for registering a basic Customer account
    AuthResponse registerCustomer(AuthRequest request);
    AuthResponse registerSuperAdmin(AuthRequest request);
    // Contract for onboarding a Seller along with shop details
    AuthResponse registerSeller(SellerRegisterRequest request);

    // Contract for verifying credentials and issuing a token
    AuthResponse login(AuthRequest request);
}