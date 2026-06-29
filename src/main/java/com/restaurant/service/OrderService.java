package com.restaurant.service;

import com.restaurant.dto.DashboardSummary.OrderSummaryResponse;
import com.restaurant.dto.Order.OrderCreateRequest;
import com.restaurant.dto.OrderItems.OrderItemCreateRequest;
import com.restaurant.dto.Order.OrderResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.entity.Product;
import com.restaurant.entity.User;
import com.restaurant.mapper.OrderMapper;
import com.restaurant.repository.OrderItemRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.ProductRepository;
import com.restaurant.repository.UserRepository;
import com.restaurant.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "totalPrice", "placed", "paid", "createdAt");
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + id
                ));

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + request.getUserId()
                ));

        Order order = new Order();
        order.setUser(user);
        order.setPlaced(true);
        order.setPaid(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemCreateRequest itemRequest : request.getItems()) {

            Product product = productRepository.findByIdForUpdate(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found with id: " + itemRequest.getProductId()
                    ));

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product is inactive: " + product.getName()
                );
            }

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Not enough stock for product: " + product.getName()
                );
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setItemTotal(itemTotal);

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(itemTotal);
        }

        orderItemRepository.saveAll(orderItems);

        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setItems(orderItems);

        Order finalOrder = orderRepository.save(savedOrder);
        finalOrder.setItems(orderItems);

        return orderMapper.toResponse(finalOrder);
    }

    public PaginatedResponse<OrderSummaryResponse> searchOrders(
            Integer userId,
            Boolean paid,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        PaginationUtils.validatePageAndSize(page, size);

        Sort sort = PaginationUtils.buildSort(
                sortBy,
                direction,
                ALLOWED_SORT_FIELDS
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage;

        if (userId != null && paid != null) {
            orderPage = orderRepository.findByUser_IdAndPaid(userId, paid, pageable);
        } else if (userId != null) {
            orderPage = orderRepository.findByUser_Id(userId, pageable);
        } else if (paid != null) {
            orderPage = orderRepository.findByPaid(paid, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        List<OrderSummaryResponse> content = orderPage.getContent()
                .stream()
                .map(orderMapper::toSummaryResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }
}