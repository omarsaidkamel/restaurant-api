package com.restaurant.service;

import com.restaurant.dto.Notification.NotificationCreateRequest;
import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.entity.Notification;
import com.restaurant.entity.Order;
import com.restaurant.mapper.NotificationMapper;
import com.restaurant.repository.NotificationRepository;
import com.restaurant.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository,
                               OrderRepository orderRepository,
                               NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.orderRepository = orderRepository;
        this.notificationMapper = notificationMapper;
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    public List<NotificationResponse> getNotificationsByOrderId(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found with id: " + orderId
            );
        }

        return notificationRepository.findByOrderId(orderId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    public NotificationResponse createNotification(NotificationCreateRequest request) {


        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + request.getOrderId()
                ));

        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setNotificationType(request.getNotificationType());
        notification.setMessage(request.getMessage());
        notification.setSentAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }

    public void createSystemNotification(Order order, String notificationType, String message) {
        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setNotificationType(notificationType);
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}