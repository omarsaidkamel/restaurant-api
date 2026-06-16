package com.restaurant.dto.Notification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Integer id;
    private Integer orderId;
    private String notificationType;
    private String message;
    private LocalDateTime sentAt;

    public NotificationResponse(Integer id, Integer orderId, String notificationType,
                                String message, LocalDateTime sentAt) {
        this.id = id;
        this.orderId = orderId;
        this.notificationType = notificationType;
        this.message = message;
        this.sentAt = sentAt;
    }
}