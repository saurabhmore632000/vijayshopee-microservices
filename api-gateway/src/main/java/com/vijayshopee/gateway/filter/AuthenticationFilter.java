package com.vijayshopee.gateway.filter;

import com.vijayshopee.gateway.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtils jwtUtils;

    public AuthenticationFilter(RouteValidator validator, JwtUtils jwtUtils) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Check if the incoming request path requires JWT validation
            if (validator.isSecured.test(request)) {

                // 2. Check if the Authorization header exists
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "Missing Authorization Header, man!", HttpStatus.UNAUTHORIZED);
                }

                // 3. Extract the Bearer Token from the header string
                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7); // Remove "Bearer " prefix
                } else {
                    return onError(exchange, "Invalid Authorization Header format, man!", HttpStatus.UNAUTHORIZED);
                }

                // 4. Validate the token signature against our secret key
                if (!jwtUtils.validateToken(authHeader)) {
                    return onError(exchange, "Unauthorized access: Invalid or expired JWT token!", HttpStatus.UNAUTHORIZED);
                }

                // 🚀 🔥 STEP 4.5: EXTRACT AND INJECT USER CONTEXT HEADERS DOWNSTREAM
                try {
                    // Unpack the user data directly from the verified token string
                    String role = jwtUtils.extractRole(authHeader);   // e.g., "ROLE_SUPER_ADMIN"
                    String email = jwtUtils.extractEmail(authHeader); // e.g., "admin@vijayshopee.com"

                    // Mutate the request to append the headers securely
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Role", role)
                            .header("X-User-Email", email)
                            .build();

                    // Pass the mutated request carrying our headers into the filter chain execution
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());

                } catch (Exception e) {
                    System.out.println("Error extracting claims from token: " + e.getMessage());
                    return onError(exchange, "Failed to parse security contexts from token, man!", HttpStatus.UNAUTHORIZED);
                }
            }

            // 5. If it's a public path (like login/register), pass it along directly
            return chain.filter(exchange);
        };
    }

    // 🛑 Helper method to send a clean reactive HTTP 401 Unauthorized status back to the client
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        System.out.println("Gateway Security Blocked: " + err);
        return response.setComplete();
    }

    public static class Config {
        // Keep this configuration class empty for now
    }
}