package com.restaurant.dto.OrderItems;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {

    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal itemTotal;

    public OrderItemResponse(Integer productId, String productName, Integer quantity, BigDecimal itemTotal) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.itemTotal = itemTotal;
    }
}