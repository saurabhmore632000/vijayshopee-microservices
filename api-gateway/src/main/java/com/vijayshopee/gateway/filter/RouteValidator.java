package com.vijayshopee.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register/superadmin",
            "/api/auth/register/customer",
            "/api/auth/register/seller",
            "/api/auth/login"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        System.out.println("Gateway checking path: " + path);

        return openApiEndpoints.stream()
                .noneMatch(uri -> path.trim().toLowerCase().contains(uri.toLowerCase()));
    };
}