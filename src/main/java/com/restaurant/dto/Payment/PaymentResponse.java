package com.restaurant.dto.Payment;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Integer id;
    private Integer orderId;
    private String paymentMethod;
    private BigDecimal originalAmount;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal finalAmount;
    private LocalDateTime paidAt;

    public PaymentResponse(Integer id, Integer orderId, String paymentMethod,
                           BigDecimal originalAmount, String discountType,
                           BigDecimal discountValue, BigDecimal finalAmount,
                           LocalDateTime paidAt) {
        this.id = id;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.originalAmount = originalAmount;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.finalAmount = finalAmount;
        this.paidAt = paidAt;
    }
}