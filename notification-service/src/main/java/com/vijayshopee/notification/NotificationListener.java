package com.vijayshopee.notification;

import com.vijayshopee.notification.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    // 🎯 Captures messages sent to 'order-topic'
    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("=================================================");
        log.info("🚀 NEW KAFKA MESSAGE INTERCEPTED!");
        log.info("Order ID: {}", event.getOrderNumber());
        log.info("Customer Email is: {}", event.getCustomerEmail());
        log.info("Total Amount Received: ₹{}", event.getTotalAmount());
        log.info("=================================================");
    }
}