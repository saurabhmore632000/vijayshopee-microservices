package com.vijayshopee.shipping.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijayshopee.shipping.dto.PaymentSuccessEvent;
import com.vijayshopee.shipping.model.Shipping;
import com.vijayshopee.shipping.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingEventListener {

    private final ShippingRepository shippingRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-events", groupId = "shipping-group")
    public void consumePaymentSuccessEvent(String message) {
        try {
            log.info("📥 [KAFKA] Intercepted raw event stream packet on topic 'payment-events'");

            // 1. Unmarshal JSON string back into the local duplicated DTO structure
            PaymentSuccessEvent event = objectMapper.readValue(message, PaymentSuccessEvent.class);
            log.info("📦 Initializing delivery allocation details for Order ID: {} | Customer Name: {}",
                    event.getOrderId(), event.getCustomerName());

            // 2. Generate a clean, unique tracking code for VijayShopee orders
            String generatedTrackingNumber = "VJ-SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 3. Build entity matching your EXACT column framework
            Shipping shipping = Shipping.builder()
                    .orderId(event.getOrderId())
                    .customerName(event.getCustomerName())
                    .shippingAddress(event.getShippingAddress())
                    .mobileNumber(event.getMobileNumber())
                    .trackingNumber(generatedTrackingNumber)
                    .shippingStatus("PENDING") // Using your specific field configuration default
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // 4. Save straight to your shippings table in PostgreSQL
            shippingRepository.save(shipping);

            log.info("✅ Row written to database! Order tracking initialized via tracker: {}", generatedTrackingNumber);
            log.info("📞 Delivery agent mobile mapping active for number: {}", event.getMobileNumber());

        } catch (Exception e) {
            log.error("❌ Failed to parse or process the payment success transaction payload", e);
        }
    }
}