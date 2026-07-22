package com.vijayshopee.payment.service;

import com.vijayshopee.payment.dto.PaymentRequestDTO;
import com.vijayshopee.payment.dto.PaymentResponseDTO;
import com.vijayshopee.payment.dto.PaymentVerifyDTO;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    PaymentResponseDTO createRazorpayOrder(PaymentRequestDTO requestDTO);

    boolean verifyPaymentSignature(PaymentVerifyDTO verifyDTO);
}
