package com.restaurant.service;

import com.restaurant.dto.DashboardSummary.DashboardSummaryResponse;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.DashboardSummary.ProductSalesReportResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.Payment;
import com.restaurant.entity.Product;
import com.restaurant.mapper.ProductMapper;
import com.restaurant.repository.OrderItemRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getDashboardSummary_shouldReturnCorrectSummary() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setPaid(true);

        Order order2 = new Order();
        order2.setId(2);
        order2.setPaid(false);

        Order order3 = new Order();
        order3.setId(3);
        order3.setPaid(true);

        Payment payment1 = new Payment();
        payment1.setId(1);
        payment1.setFinalAmount(BigDecimal.valueOf(250));

        Payment payment2 = new Payment();
        payment2.setId(2);
        payment2.setFinalAmount(BigDecimal.valueOf(150));

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3));
        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));

        DashboardSummaryResponse result = reportService.getDashboardSummary();

        assertNotNull(result);
        assertEquals(3, result.getTotalOrders());
        assertEquals(2, result.getPaidOrders());
        assertEquals(1, result.getUnpaidOrders());
        assertEquals(0, BigDecimal.valueOf(400).compareTo(result.getTotalRevenue()));

        verify(orderRepository).findAll();
        verify(paymentRepository).findAll();
    }

    @Test
    void getProductSalesReport_shouldReturnProductSalesReport() {
        ProductSalesReportResponse report1 = new ProductSalesReportResponse(
                1,
                "Shrimp",
                5L,
                BigDecimal.valueOf(1250)
        );

        ProductSalesReportResponse report2 = new ProductSalesReportResponse(
                2,
                "Fish",
                3L,
                BigDecimal.valueOf(360)
        );

        when(orderItemRepository.getProductSalesReport())
                .thenReturn(List.of(report1, report2));

        List<ProductSalesReportResponse> result = reportService.getProductSalesReport();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1, result.get(0).getProductId());
        assertEquals("Shrimp", result.get(0).getProductName());
        assertEquals(5L, result.get(0).getTotalQuantity());
        assertEquals(0, BigDecimal.valueOf(1250).compareTo(result.get(0).getTotalSales()));

        verify(orderItemRepository).getProductSalesReport();
    }

    @Test
    void getLowStockProducts_shouldReturnLowStockProducts() {
        Product product = new Product();
        product.setId(1);
        product.setName("Shrimp");
        product.setPrice(BigDecimal.valueOf(250));
        product.setStock(3);
        product.setCategory("Seafood");
        product.setActive(true);

        ProductResponse response = new ProductResponse();
        response.setId(1);
        response.setName("Shrimp");
        response.setPrice(BigDecimal.valueOf(250));
        response.setStock(3);
        response.setCategory("Seafood");
        response.setActive(true);

        when(productRepository.findByActiveTrueAndStockLessThanEqual(5))
                .thenReturn(List.of(product));

        when(productMapper.toResponse(product)).thenReturn(response);

        List<ProductResponse> result = reportService.getLowStockProducts(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Shrimp", result.get(0).getName());
        assertEquals(3, result.get(0).getStock());
        assertTrue(result.get(0).getActive());

        verify(productRepository).findByActiveTrueAndStockLessThanEqual(5);
        verify(productMapper).toResponse(product);
    }
}