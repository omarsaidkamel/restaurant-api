package com.restaurant.dto.DashboardSummary;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderSummaryResponse {

    private Integer id;
    private Integer userId;
    private String userName;
    private BigDecimal totalPrice;
    private Boolean placed;
    private Boolean paid;
    private LocalDateTime createdAt;
    private Integer itemsCount;

    public OrderSummaryResponse(Integer id, Integer userId, String userName,
                                BigDecimal totalPrice, Boolean placed,
                                Boolean paid, LocalDateTime createdAt,
                                Integer itemsCount) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.placed = placed;
        this.paid = paid;
        this.createdAt = createdAt;
        this.itemsCount = itemsCount;
    }

}