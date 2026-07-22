package com.vijayshopee.auth.service;


import com.vijayshopee.auth.dto.AuthRequest;
import com.vijayshopee.auth.dto.AuthResponse;
import com.vijayshopee.auth.dto.SellerRegisterRequest;
import com.vijayshopee.auth.model.Role;
import com.vijayshopee.auth.model.User;
import com.vijayshopee.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 💡 Automatically generates constructor injection for final fields
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse registerSuperAdmin(AuthRequest request) {
        // 🛑 1. Validation check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered, man!");
        }

        // 🏗️ 2. Build Super Admin entity
        User superAdmin = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt password
                .role(Role.ROLE_SUPERADMIN)
                .build();

        // 💾 3. Save into PostgreSQL/MySQL
        userRepository.save(superAdmin);

        // 🔑 4. Generate security token
        String jwtToken = jwtService.generateToken(superAdmin);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(superAdmin.getEmail())
                .role(superAdmin.getRole().name())
                .build();
    }


    @Override
    public AuthResponse registerCustomer(AuthRequest request) {
        // 🛑 1. Validation check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered, man!");
        }

        User customer = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt password before DB save
                .role(Role.ROLE_CUSTOMER)
                .build();

        // 💾 3. Save into PostgreSQL
        userRepository.save(customer);

        // 🔑 4. Generate security token and return payload
        String jwtToken = jwtService.generateToken(customer);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(customer.getEmail())
                .role(customer.getRole().name())
                .build();
    }

    @Override
    public AuthResponse registerSeller(SellerRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered, man!");
        }

        User seller = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_SELLER)
                .build();

        userRepository.save(seller);

        // 🔥 Note for later: Here we will drop an async message to the message broker
        // to create their profile inside 'product-service' with shopName and shopDescription!

        String jwtToken = jwtService.generateToken(seller);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(seller.getEmail())
                .role(seller.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // 🔍 1. Fetch user by email address
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email!"));

        // 🔐 2. Verify if the raw password matches the database hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password credentials!");
        }

        // 🔑 3. Authentication successful, issue token
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}