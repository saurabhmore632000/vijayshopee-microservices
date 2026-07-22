package com.vijayshopee.order.service;

import com.vijayshopee.order.client.ProductClient;
import com.vijayshopee.order.dto.OrderRequest;
import com.vijayshopee.order.dto.OrderResponse;
import com.vijayshopee.order.dto.ProductResponse;
import com.vijayshopee.order.event.OrderPlacedEvent;
import com.vijayshopee.order.event.PaymentSuccessEvent;
import com.vijayshopee.order.model.Order;
import com.vijayshopee.order.model.OrderItem;
import com.vijayshopee.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest, String email) {
        ProductResponse product = productClient.getProductById(orderRequest.getProductId());
        Double calculateTotal = product.getPrice() * orderRequest.getQuantity();

        Order order = Order.builder()
                .email(email)
                .customerName(orderRequest.getCustomerName())
                .shippingAddress(orderRequest.getShippingAddress())
                .mobileNumber(orderRequest.getMobileNumber())
                .totalAmount(calculateTotal)
                .orderDate(LocalDateTime.now())
                .status("Pending")
                .build();

        OrderItem orderItem = OrderItem.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .price(product.getPrice())
                .order(order)
                .build();

        order.setOrderItems(List.of(orderItem));

        Order savedOrders = orderRepository.save(order);

        OrderPlacedEvent eventData = new OrderPlacedEvent(
                savedOrders.getId().toString(), // Converting orderId to String for the payload tracking
                savedOrders.getEmail(),
                savedOrders.getTotalAmount()
        );

        try {
            log.info("Sending OrderPlacedEvent to Kafka for Order ID: {}", savedOrders.getId());
            kafkaTemplate.send("order-topic", eventData);
            log.info("OrderPlacedEvent published successfully!");
        } catch (Exception e) {
            log.error("Failed to route order notification event to Kafka broker", e);
        }

        return OrderResponse.builder()
                .orderId(savedOrders.getId())
                .email(savedOrders.getEmail())
                .customerName(savedOrders.getCustomerName())
                .shippingAddress(savedOrders.getShippingAddress())
                .mobileNumber(savedOrders.getMobileNumber())
                .totalAmount(savedOrders.getTotalAmount())
                .status(savedOrders.getStatus())
                .orderDate(savedOrders.getOrderDate())
                .build();
    }

    @Override
    public void updateStatus(Long orderId, String status) {
        // 1. Fetch the order row from your PostgreSQL table
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with ID: " + orderId));

        // 2. Change the status (e.g., from "Pending" to "Paid")
        order.setStatus(status);

        // 3. Save the updated row back to the orders database
        Order savedOrder = orderRepository.save(order);

        log.info("🔄 Order status updated to '{}' in database for Order ID: {}", status, orderId);

        if("paid".equalsIgnoreCase(status)){
            try{
                PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                        .orderId(savedOrder.getId())
                        .customerName(savedOrder.getCustomerName())       // Real name from DB row
                        .shippingAddress(savedOrder.getShippingAddress())   // Real address from DB row
                        .mobileNumber(savedOrder.getMobileNumber())         // Real mobile number from DB row
                        .paymentStatus("SUCCESS")
                        .totalAmount(savedOrder.getTotalAmount())
                        .build();

                log.info("📤 [KAFKA] Firing complete PaymentSuccessEvent to 'payment-events' topic for shipping fulfillment...");

                // Send the serialized event data packet using the order ID as a message key
                kafkaTemplate.send("payment-events", String.valueOf(savedOrder.getId()), event);

                log.info("✅ Shipping payload successfully routed to broker from order-service!");
            }catch (Exception e){
                log.error("❌ Failed to broadcast PaymentSuccessEvent to Kafka for Order ID: {}", savedOrder.getId(), e);
            };
        }
    }
}
