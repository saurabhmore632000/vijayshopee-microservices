package com.vijayshopee.auth.controller;

import com.vijayshopee.auth.dto.AuthRequest;
import com.vijayshopee.auth.dto.AuthResponse;
import com.vijayshopee.auth.dto.SellerRegisterRequest;
import com.vijayshopee.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🌐 1. Endpoint for registering a customer account
    @PostMapping("/register/superadmin")
    public ResponseEntity<AuthResponse> registerSuperAdmin(@RequestBody AuthRequest request) {
        AuthResponse response = authService.registerSuperAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponse> registerCustomer(@RequestBody AuthRequest request) {
        AuthResponse response = authService.registerCustomer(request);
        return ResponseEntity.ok(response);
    }

    // 🌐 2. Endpoint for onboarding a seller account
    @PostMapping("/register/seller")
    public ResponseEntity<AuthResponse> registerSeller(@RequestBody SellerRegisterRequest request) {
        AuthResponse response = authService.registerSeller(request);
        return ResponseEntity.ok(response);
    }

    // 🌐 3. Endpoint for signing in users and generating tokens
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}