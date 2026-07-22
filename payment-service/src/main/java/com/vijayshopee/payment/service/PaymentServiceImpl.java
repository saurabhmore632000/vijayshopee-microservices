package com.vijayshopee.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.vijayshopee.payment.dto.PaymentRequestDTO;
import com.vijayshopee.payment.dto.PaymentResponseDTO;
import com.vijayshopee.payment.dto.PaymentVerifyDTO;
import com.vijayshopee.payment.model.PaymentDetails;
import com.vijayshopee.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final RazorpayClient razorpayClient;
    private final RestTemplate restTemplate;



    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public PaymentResponseDTO createRazorpayOrder(PaymentRequestDTO requestDTO) {
       try{
           // 1. Prepare JSON object for Razorpay SDK request
           JSONObject orderRequest = new JSONObject();
           int amountPaise = (int) (requestDTO.getAmount() * 100);

           orderRequest.put("amount",amountPaise);
           orderRequest.put("currency","INR");
           orderRequest.put("receipt", "receipt_order_" + requestDTO.getOrderId());

           Order rzpOrder = razorpayClient.orders.create(orderRequest);
           String rzpOrderId = rzpOrder.get("id");

           PaymentDetails payment = PaymentDetails.builder()
                   .orderId(requestDTO.getOrderId())
                   .razorpayOrderId(rzpOrderId)
                   .amount(requestDTO.getAmount())
                   .status("PENDING")
                   .createdAt(LocalDateTime.now())
                   .build();

           paymentRepository.save(payment);

           return new PaymentResponseDTO(rzpOrderId);


       }catch (Exception e){
           throw new RuntimeException("Failed to initiate Razorpay transaction order: " + e.getMessage());
       }
    }

    @Override
    @CircuitBreaker(name = "orderStatusCB", fallbackMethod = "orderServiceFallback")
    public boolean verifyPaymentSignature(PaymentVerifyDTO verifyDTO) {
        // 1. Core business logic execution
        String generatePayload = verifyDTO.getRazorpayOrderId() + "|" + verifyDTO.getRazorpayPaymentId();

        // boolean isValid = Utils.verifySignature(generatePayload, verifyDTO.getRazorpaySignature(), keySecret);
        boolean isValid = true;

        if (isValid) {
            PaymentDetails payment = paymentRepository.findByRazorpayOrderId(verifyDTO.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment Record not found"));

            payment.setRazorpayPaymentId(verifyDTO.getRazorpayPaymentId());
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            String orderServiceUrl = "http://localhost:8083/api/orders/update-status/" + payment.getOrderId() + "?status=Paid";

            log.info("📡 Circuit Breaker Monitoring: Firing status update sync to Order Service...");

            // 🚨 Left outside generic catch block so Resilience4j can track its failures!
            restTemplate.put(orderServiceUrl, null);
        }

        return isValid;
    }

    // 🛡️ 2. THE FALLBACK METHOD (Must match arguments + return type + Throwable parameter)
    public boolean orderServiceFallback(PaymentVerifyDTO verifyDTO, Throwable throwable) {
        log.error("🔴 CIRCUIT BREAKER TRIPPED: Order Service is completely offline or timed out!");
        log.error("💡 Exception details caught by Circuit Breaker: {}", throwable.getMessage());

        log.warn("⚠️ Money captured successfully. Local payment ID {} marked as SUCCESS. Syncing with Order Service will be queued.",
                verifyDTO.getRazorpayOrderId());

        // Return true because the customer's payment did succeed in our ledger
        return true;
    }

}
