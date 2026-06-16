package com.restaurant.dto.DashboardSummary;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSalesReportResponse {

    private Integer productId;
    private String productName;
    private Long totalQuantity;
    private BigDecimal totalSales;

    public ProductSalesReportResponse(Integer productId, String productName,
                                      Long totalQuantity, BigDecimal totalSales) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalSales = totalSales;
    }

}