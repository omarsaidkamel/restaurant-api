package com.restaurant.controller;

import com.restaurant.dto.DashboardSummary.DashboardSummaryResponse;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.DashboardSummary.ProductSalesReportResponse;
import com.restaurant.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "APIs for dashboard and sales reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Get dashboard summary")
    @GetMapping("/summary")
    public DashboardSummaryResponse getDashboardSummary() {
        return reportService.getDashboardSummary();
    }

    @Operation(summary = "Get product sales report")
    @GetMapping("/product-sales")
    public List<ProductSalesReportResponse> getProductSalesReport() {
        return reportService.getProductSalesReport();
    }

    @Operation(summary = "Get low stock products")
    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer stockLimit
    ) {
        return reportService.getLowStockProducts(stockLimit);
    }
}