package com.restaurant.service;

import com.restaurant.dto.Order.OrderCreateRequest;
import com.restaurant.dto.OrderItems.OrderItemCreateRequest;
import com.restaurant.entity.User;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.ProductRepository;
import com.restaurant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldThrowExceptionWhenUserNotFound() {
        OrderItemCreateRequest itemRequest = new OrderItemCreateRequest();
        itemRequest.setProductId(1);
        itemRequest.setQuantity(2);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(999);
        request.setItems(List.of(itemRequest));

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("User not found with id: 999", exception.getReason());

        verify(userRepository).findById(999);
        verifyNoInteractions(productRepository);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldThrowExceptionWhenUserIsInactive() {
        User user = new User();
        user.setId(2);
        user.setName("Ahmed");
        user.setActive(false);

        OrderItemCreateRequest itemRequest = new OrderItemCreateRequest();
        itemRequest.setProductId(1);
        itemRequest.setQuantity(2);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(2);
        request.setItems(List.of(itemRequest));

        when(userRepository.findById(2)).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("User is inactive: Ahmed", exception.getReason());

        verify(userRepository).findById(2);
        verifyNoInteractions(productRepository);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldThrowExceptionWhenProductNotFound() {
        User user = new User();
        user.setId(2);
        user.setName("Ahmed");
        user.setActive(true);

        OrderItemCreateRequest itemRequest = new OrderItemCreateRequest();
        itemRequest.setProductId(999);
        itemRequest.setQuantity(2);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(2);
        request.setItems(List.of(itemRequest));

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(productRepository.findByIdForUpdate(999)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Product not found with id: 999", exception.getReason());

        verify(userRepository).findById(2);
        verify(productRepository).findByIdForUpdate(999);
        verify(orderRepository).save(any());
    }
}