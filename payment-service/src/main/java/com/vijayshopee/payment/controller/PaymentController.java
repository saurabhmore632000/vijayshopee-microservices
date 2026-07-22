package com.vijayshopee.payment.controller;

import com.vijayshopee.payment.dto.PaymentRequestDTO;
import com.vijayshopee.payment.dto.PaymentResponseDTO;
import com.vijayshopee.payment.dto.PaymentVerifyDTO;
import com.vijayshopee.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDTO> createPaymentOrder(@RequestBody PaymentRequestDTO requestDTO){
        PaymentResponseDTO res = paymentService.createRazorpayOrder(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerifyDTO verifyDTO){
        boolean isVerified = paymentService.verifyPaymentSignature(verifyDTO);

        if (isVerified) {
            return ResponseEntity.ok("Payment verified successfully and order status updated!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment signature validation.");
        }
    }

}
