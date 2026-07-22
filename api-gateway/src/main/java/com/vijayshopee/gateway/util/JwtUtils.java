package com.vijayshopee.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtils {

    // 🔑 Pulls the exact same secret key you defined in your configurations
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String extractRole(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("role", String.class); // 💡 Must match the map key string you used in auth-service!
    }

    // Extract email profile string
    public String extractEmail(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.getSubject(); // Since email is typically set as the Subject claim
    }

    // Helper method to parse token body details
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Uses your shared jwt.secret byte key arrays
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🛠️ Validates the token signature and expiration date
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT Token Validation Failed, man: " + e.getMessage());
            return false;
        }
    }

    // 🔍 Extracts user profile claims (like email, roles) from the token body string
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}