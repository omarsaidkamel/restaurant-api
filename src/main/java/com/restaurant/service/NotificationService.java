package com.restaurant.service;

import com.restaurant.dto.Notification.NotificationCreateRequest;
import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.entity.Notification;
import com.restaurant.entity.Order;
import com.restaurant.mapper.NotificationMapper;
import com.restaurant.repository.NotificationRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.util.BusinessValidationUtils;
import com.restaurant.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "notificationType", "sentAt");
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

        BusinessValidationUtils.validateNotificationType(request.getNotificationType());

        String notificationType =
                BusinessValidationUtils.normalize(request.getNotificationType());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + request.getOrderId()
                ));

        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setNotificationType(notificationType);
        notification.setMessage(request.getMessage());
        notification.setSentAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }

    public void createSystemNotification(Order order, String notificationType, String message) {
        BusinessValidationUtils.validateNotificationType(notificationType);

        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setNotificationType(
                BusinessValidationUtils.normalize(notificationType)
        );
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public PaginatedResponse<NotificationResponse> searchNotifications(
            Integer orderId,
            String notificationType,
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
        String normalizedNotificationType = normalize(notificationType);

        Page<Notification> notificationPage;

        if (orderId != null && normalizedNotificationType != null) {
            notificationPage = notificationRepository.findByOrder_IdAndNotificationTypeIgnoreCase(
                    orderId,
                    normalizedNotificationType,
                    pageable
            );
        } else if (orderId != null) {
            notificationPage = notificationRepository.findByOrder_Id(
                    orderId,
                    pageable
            );
        } else if (normalizedNotificationType != null) {
            notificationPage = notificationRepository.findByNotificationTypeIgnoreCase(
                    normalizedNotificationType,
                    pageable
            );
        } else {
            notificationPage = notificationRepository.findAll(pageable);
        }

        List<NotificationResponse> content = notificationPage.getContent()
                .stream()
                .map(notificationMapper::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.isLast()
        );
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}