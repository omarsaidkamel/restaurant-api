package com.restaurant.controller;

import com.restaurant.dto.Payment.PaymentCreateRequest;
import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Integer id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    public PaymentResponse payOrder(@Valid @RequestBody PaymentCreateRequest request) {
        return paymentService.payOrder(request);
    }
}