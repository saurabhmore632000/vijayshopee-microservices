package com.vijayshopee.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, unique = true)
    private String razorpayOrderId;  // Razorpay order reference (order_PtK7u9Xz2mAB4c)

    private String razorpayPaymentId;// Razorpay receipt capture reference (pay_MockId93248)

    @Column(nullable = false)
    private Double amount;           // Total amount paid in Rupees

    @Column(nullable = false)
    private String status;           // PENDING, SUCCESS, FAILED

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
