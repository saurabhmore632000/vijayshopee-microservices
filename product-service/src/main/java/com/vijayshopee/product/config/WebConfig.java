package com.vijayshopee.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allows all paths in your product service
                        .allowedOrigins("http://localhost:5173") // Your React Dev server port
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS is crucial for preflight!
                        .allowedHeaders("Authorization", "Content-Type", "X-User-Role") // 🎯 MUST explicitly allow your custom header!
                        .exposedHeaders("Authorization")
                        .allowCredentials(true);
            }
        };
    }
}
