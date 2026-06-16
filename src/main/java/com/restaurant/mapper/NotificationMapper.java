package com.restaurant.mapper;

import com.restaurant.dto.Notification.NotificationResponse;
import com.restaurant.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getOrder().getId(),
                notification.getNotificationType(),
                notification.getMessage(),
                notification.getSentAt()
        );
    }
}