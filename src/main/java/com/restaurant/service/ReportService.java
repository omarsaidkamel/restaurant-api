package com.restaurant.service;

import com.restaurant.dto.DashboardSummary.DashboardSummaryResponse;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.DashboardSummary.ProductSalesReportResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.Payment;
import com.restaurant.mapper.ProductMapper;
import com.restaurant.repository.OrderItemRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ReportService(OrderRepository orderRepository,
                         PaymentRepository paymentRepository,
                         OrderItemRepository orderItemRepository,
                         ProductRepository productRepository,
                         ProductMapper productMapper) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        List<Order> orders = orderRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        long totalOrders = orders.size();

        long paidOrders = orders.stream()
                .filter(order -> Boolean.TRUE.equals(order.getPaid()))
                .count();

        long unpaidOrders = totalOrders - paidOrders;

        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardSummaryResponse(
                totalOrders,
                paidOrders,
                unpaidOrders,
                totalRevenue
        );
    }

    public List<ProductSalesReportResponse> getProductSalesReport() {
        return orderItemRepository.getProductSalesReport();
    }

    public List<ProductResponse> getLowStockProducts(Integer stockLimit) {
        return productRepository.findByStockLessThanEqual(stockLimit)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}