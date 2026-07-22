package com.vijayshopee.payment.repository;

import com.vijayshopee.payment.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDetails,Long> {
    Optional<PaymentDetails> findByRazorpayOrderId(String razorpayOrderId);
}
