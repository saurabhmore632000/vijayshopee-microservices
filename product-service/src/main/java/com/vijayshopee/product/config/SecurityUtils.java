package com.vijayshopee.product.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

    public static void requireRole(String actualRole, String requiredRole) {
        // If the header wasn't passed by the gateway or doesn't match, block them!
        if (actualRole == null || !actualRole.equalsIgnoreCase(requiredRole)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access Denied, man! You do not have permission to access this resource."
            );
        }
    }
}