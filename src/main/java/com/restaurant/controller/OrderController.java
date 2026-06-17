package com.restaurant.controller;

import com.restaurant.dto.DashboardSummary.OrderSummaryResponse;
import com.restaurant.dto.Order.OrderCreateRequest;
import com.restaurant.dto.Order.OrderResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/search")
    public PaginatedResponse<OrderSummaryResponse> searchOrders(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return orderService.searchOrders(
                userId,
                paid,
                page,
                size,
                sortBy,
                direction
        );
    }
}