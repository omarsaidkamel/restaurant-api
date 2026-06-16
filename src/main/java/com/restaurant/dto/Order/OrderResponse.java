package com.restaurant.dto.Order;

import com.restaurant.dto.OrderItems.OrderItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Integer id;
    private Integer userId;
    private String userName;
    private BigDecimal totalPrice;
    private Boolean placed;
    private Boolean paid;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(Integer id, Integer userId, String userName,
                         BigDecimal totalPrice, Boolean placed, Boolean paid,
                         LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.placed = placed;
        this.paid = paid;
        this.createdAt = createdAt;
        this.items = items;
    }

}