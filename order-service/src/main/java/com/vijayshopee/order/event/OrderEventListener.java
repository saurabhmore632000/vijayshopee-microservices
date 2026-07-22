package com.vijayshopee.order.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijayshopee.order.dto.OrderShippedEvent;
import com.vijayshopee.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "shipping-events", groupId = "order-shipping-group")
    @Transactional
    public void listenShippingUpdate(String message) {
        try {
            // 1. Parse incoming shipping status update
            OrderShippedEvent event = objectMapper.readValue(message, OrderShippedEvent.class);
            log.info("📥 [KAFKA] Received shipping update for Order ID: {}", event.getOrderId());

            // 2. Fetch the actual customer order record and rewrite status
            orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
                order.setStatus("SHIPPED"); // Or "DISPATCHED" to match your styling preference
                orderRepository.save(order);
                log.info("✅ Order ID {} updated to SHIPPED in Order database!", event.getOrderId());
            }, () -> log.error("❌ Received shipping event but Order ID {} not found!", event.getOrderId()));

        } catch (Exception e) {
            log.error("❌ Failed to process shipping event message", e);
        }
    }
}