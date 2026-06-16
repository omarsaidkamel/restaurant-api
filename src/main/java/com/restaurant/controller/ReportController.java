package com.restaurant.controller;

import com.restaurant.dto.DashboardSummary.DashboardSummaryResponse;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.DashboardSummary.ProductSalesReportResponse;
import com.restaurant.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getDashboardSummary() {
        return reportService.getDashboardSummary();
    }

    @GetMapping("/product-sales")
    public List<ProductSalesReportResponse> getProductSalesReport() {
        return reportService.getProductSalesReport();
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer stockLimit
    ) {
        return reportService.getLowStockProducts(stockLimit);
    }
}