package com.restaurant.repository;

import com.restaurant.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByOrderId(Integer orderId);

    Page<Notification> findByOrder_Id(Integer orderId, Pageable pageable);

    Page<Notification> findByNotificationTypeIgnoreCase(String notificationType, Pageable pageable);

    Page<Notification> findByOrder_IdAndNotificationTypeIgnoreCase(
            Integer orderId,
            String notificationType,
            Pageable pageable
    );
}