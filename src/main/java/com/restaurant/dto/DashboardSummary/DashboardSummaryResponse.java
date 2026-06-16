package com.restaurant.dto.DashboardSummary;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardSummaryResponse {

    private long totalOrders;
    private long paidOrders;
    private long unpaidOrders;
    private BigDecimal totalRevenue;

    public DashboardSummaryResponse(long totalOrders, long paidOrders,
                                    long unpaidOrders, BigDecimal totalRevenue) {
        this.totalOrders = totalOrders;
        this.paidOrders = paidOrders;
        this.unpaidOrders = unpaidOrders;
        this.totalRevenue = totalRevenue;
    }

}