package com.restaurant.controller;

import com.restaurant.dto.PaginatedResponse;
import com.restaurant.dto.Payment.PaymentCreateRequest;
import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "APIs for order payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @Operation(summary = "Search payments with pagination")
    @GetMapping("/search")
    public PaginatedResponse<PaymentResponse> searchPayments(
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "paidAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return paymentService.searchPayments(
                orderId,
                paymentMethod,
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Integer id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> payOrder(
            @Valid @RequestBody PaymentCreateRequest request
    ) {
        PaymentResponse response = paymentService.payOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}