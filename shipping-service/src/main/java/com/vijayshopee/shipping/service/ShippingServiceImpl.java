package com.vijayshopee.shipping.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijayshopee.shipping.client.InventoryClient;
import com.vijayshopee.shipping.dto.OrderShippedEvent;
import com.vijayshopee.shipping.dto.ShippingResponse;
import com.vijayshopee.shipping.model.Shipping;
import com.vijayshopee.shipping.repository.ShippingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService{
    private final ShippingRepository shippingRepository;
    private final InventoryClient inventoryClient;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ShippingResponse getShippingByOrderId(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(()->new RuntimeException("Shipping record not found for Order ID: " + orderId));
        return mapToResponse(shipping);
    }

    @Override
    @Transactional // 💡 Wrap in transactional so database updates roll back if things fail
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "handleInventoryFallback")
    public ShippingResponse shipOrder(Long orderId, Long productId, Integer quantity) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping row missing for Order ID: " + orderId));

        if ("DISPATCHED".equals(shipping.getShippingStatus())) {
            throw new IllegalStateException("Order ID " + orderId + " has already been dispatched!");
        }

        inventoryClient.deductStock(productId, quantity);
        log.info("✅ Inventory service successfully deducted {} units for Product ID: {}", quantity, productId);

        // 3. Update shipping state flags safely
        shipping.setShippingStatus("DISPATCHED");
        shipping.setUpdatedAt(LocalDateTime.now());

        Shipping savedShipping = shippingRepository.save(shipping);
        log.info("📦 Order ID {} successfully marked as DISPATCHED in the warehouse database.", orderId);

        try {
            OrderShippedEvent event = new OrderShippedEvent(
                    shipping.getOrderId(),
                    "SHIPPED",
                    shipping.getTrackingNumber()
            );
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("shipping-events", message);
            log.info("📢 Published OrderShippedEvent to Kafka for Order: {}", orderId);
        } catch (Exception e) {
            log.error("Failed to broadcast shipping event to Kafka", e);
        }

        return mapToResponse(savedShipping);
    }

    public ShippingResponse handleInventoryFallback(Long orderId, Long productId, Integer quantity, Throwable throwable) {
        log.error("❌ Inventory Service is down or timed out! Firing fallback method. Reason: {}", throwable.getMessage());

        // Throwing an informative exception back to the frontend/Postman
        throw new RuntimeException("Cannot complete shipment right now. Inventory system is unreachable. Please try again later.");
    }


    private ShippingResponse mapToResponse(Shipping shipping) {
        return ShippingResponse.builder()
                .id(shipping.getId())
                .orderId(shipping.getOrderId())
                .customerName(shipping.getCustomerName())
                .shippingAddress(shipping.getShippingAddress())
                .mobileNumber(shipping.getMobileNumber())
                .trackingNumber(shipping.getTrackingNumber())
                .shippingStatus(shipping.getShippingStatus())
                .createdAt(shipping.getCreatedAt())
                .updatedAt(shipping.getUpdatedAt())
                .build();
    }
}
