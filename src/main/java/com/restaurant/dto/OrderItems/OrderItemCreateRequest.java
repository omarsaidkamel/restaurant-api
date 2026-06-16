package com.restaurant.dto.OrderItems;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemCreateRequest {

    @NotNull(message = "Order item id is required")
    private Integer id;

    @NotNull(message = "Product id is required")
    private Integer productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}