package com.restaurant.controller;

import com.restaurant.dto.Notification.NotificationCreateRequest;
import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/search")
    public PaginatedResponse<NotificationResponse> searchNotifications(
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) String notificationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return notificationService.searchNotifications(
                orderId,
                notificationType,
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/order/{orderId}")
    public List<NotificationResponse> getNotificationsByOrderId(@PathVariable Integer orderId) {
        return notificationService.getNotificationsByOrderId(orderId);
    }

    @PostMapping
    public NotificationResponse createNotification(
            @Valid @RequestBody NotificationCreateRequest request
    ) {
        return notificationService.createNotification(request);
    }
}