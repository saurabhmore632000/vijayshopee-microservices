package com.vijayshopee.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    // 1️⃣ Extract the username/email string from a token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2️⃣ Generic helper to extract any single claim from the token payload
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 3️⃣ Entry method to generate a brand new token using a user's details
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Extract the role authority string (e.g., ROLE_SELLER) and inject it into the map
        String role = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("");

        extraClaims.put("role", role);
        return generateToken(extraClaims, userDetails);
    }

    // 4️⃣ Internal method that builds the token with claims, dates, and signatures
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Sets "sub" (the user's email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token creation timestamp
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24-hour expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with HMAC-SHA256
                .compact(); // Pack everything into a single cryptographic string
    }

    // 5️⃣ Decrypts and reads the entire claims body using our signing key
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 6️⃣ Decodes our Hex/Base64 secret string from application.properties into a usable cryptographic key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}