package com.vijayshopee.shipping.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    private String mobileNumber;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "shipping_status", nullable = false)
    private String shippingStatus; // "PENDING", "DISPATCHED", "DELIVERED"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
