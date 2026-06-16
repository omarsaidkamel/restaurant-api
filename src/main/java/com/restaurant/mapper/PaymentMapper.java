package com.restaurant.mapper;

import com.restaurant.dto.Payment.PaymentResponse;
import com.restaurant.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentMethod(),
                payment.getOriginalAmount(),
                payment.getDiscountType(),
                payment.getDiscountValue(),
                payment.getFinalAmount(),
                payment.getPaidAt()
        );
    }
}