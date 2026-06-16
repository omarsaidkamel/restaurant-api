package com.restaurant.dto.Payment;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentCreateRequest {

    @NotNull(message = "Payment id is required")
    private Integer id;

    @NotNull(message = "Order id is required")
    private Integer orderId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Discount type is required")
    private String discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.00", message = "Discount value must not be negative")
    private BigDecimal discountValue;
}