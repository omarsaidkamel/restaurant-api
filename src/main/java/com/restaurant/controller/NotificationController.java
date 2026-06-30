package com.restaurant.controller;

import com.restaurant.dto.Notification.NotificationCreateRequest;
import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.dto.PaginatedResponse;
import com.restaurant.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "APIs for order notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @Operation(summary = "Search notifications with pagination")
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

    @Operation(summary = "Get notifications by order id")
    @GetMapping("/order/{orderId}")
    public List<NotificationResponse> getNotificationsByOrderId(@PathVariable Integer orderId) {
        return notificationService.getNotificationsByOrderId(orderId);
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationCreateRequest request
    ) {
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}