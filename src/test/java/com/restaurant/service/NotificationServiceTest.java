package com.restaurant.service;

import com.restaurant.dto.Notification.NotificationCreateRequest;
import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.entity.Notification;
import com.restaurant.entity.Order;
import com.restaurant.mapper.NotificationMapper;
import com.restaurant.repository.NotificationRepository;
import com.restaurant.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotification_shouldThrowExceptionWhenOrderNotFound() {
        NotificationCreateRequest request = new NotificationCreateRequest();
        request.setOrderId(999);
        request.setNotificationType("email");
        request.setMessage("Order created successfully");

        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> notificationService.createNotification(request)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Order not found with id: 999", exception.getReason());

        verify(orderRepository).findById(999);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void createNotification_shouldCreateNotificationSuccessfully() {
        Order order = new Order();
        order.setId(1);

        NotificationCreateRequest request = new NotificationCreateRequest();
        request.setOrderId(1);
        request.setNotificationType("email");
        request.setMessage("Order paid successfully");

        NotificationResponse response = new NotificationResponse();
        response.setId(1);
        response.setOrderId(1);
        response.setNotificationType("email");
        response.setMessage("Order paid successfully");
        response.setSentAt(LocalDateTime.now());

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1);
            return notification;
        });

        when(notificationMapper.toResponse(any(Notification.class))).thenReturn(response);

        NotificationResponse result = notificationService.createNotification(request);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getOrderId());
        assertEquals("email", result.getNotificationType());
        assertEquals("Order paid successfully", result.getMessage());

        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(order, savedNotification.getOrder());
        assertEquals("email", savedNotification.getNotificationType());
        assertEquals("Order paid successfully", savedNotification.getMessage());
        assertNotNull(savedNotification.getSentAt());

        verify(orderRepository).findById(1);
    }
}