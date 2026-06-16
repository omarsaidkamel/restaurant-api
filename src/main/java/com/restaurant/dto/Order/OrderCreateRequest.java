package com.restaurant.dto.Order;

import com.restaurant.dto.OrderItems.OrderItemCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {

    @NotNull(message = "User id is required")
    private Integer userId;

    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemCreateRequest> items;
}