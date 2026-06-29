package com.restaurant.repository;

import com.restaurant.dto.DashboardSummary.ProductSalesReportResponse;
import com.restaurant.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
    @Query("""
            SELECT new com.restaurant.dto.DashboardSummary.ProductSalesReportResponse(
                p.id,
                p.name,
                SUM(oi.quantity),
                SUM(oi.itemTotal)
            )
            FROM OrderItem oi
            JOIN oi.product p
            GROUP BY p.id, p.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<ProductSalesReportResponse> getProductSalesReport();

    boolean existsByProduct_Id(Integer productId);
}
