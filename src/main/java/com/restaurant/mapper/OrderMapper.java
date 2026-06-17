package com.restaurant.mapper;

import com.restaurant.dto.DashboardSummary.OrderSummaryResponse;
import com.restaurant.dto.OrderItems.OrderItemResponse;
import com.restaurant.dto.Order.OrderResponse;
import com.restaurant.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getItemTotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getTotalPrice(),
                order.getPlaced(),
                order.getPaid(),
                order.getCreatedAt(),
                items
        );
    }

    public OrderSummaryResponse toSummaryResponse(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getTotalPrice(),
                order.getPlaced(),
                order.getPaid(),
                order.getCreatedAt(),
                order.getItems() == null ? 0 : order.getItems().size()
        );
    }
}