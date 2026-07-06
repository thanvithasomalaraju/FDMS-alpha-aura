package com.fdms.controller;

import com.fdms.dto.PaymentDto;
import com.fdms.dto.ProcessPaymentRequest;
import com.fdms.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentDto> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Processing payment for order ID: {}", request.getOrderId());
        PaymentDto payment = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDto> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Getting payment for order ID: {}", orderId);
        PaymentDto payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        log.info("Getting payment by ID: {}", id);
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> refundPayment(@PathVariable Long id) {
        log.info("Refunding payment with ID: {}", id);
        PaymentDto refundedPayment = paymentService.refundPayment(id);
        return ResponseEntity.ok(refundedPayment);
    }
}
