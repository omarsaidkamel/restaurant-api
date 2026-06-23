package com.restaurant.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public final class BusinessValidationUtils {

    private static final Set<String> PAYMENT_METHODS =
            Set.of("cash", "card", "wallet");

    private static final Set<String> DISCOUNT_TYPES =
            Set.of("none", "fixed", "percentage");

    private static final Set<String> NOTIFICATION_TYPES =
            Set.of("email", "sms", "app");

    private BusinessValidationUtils() {
    }

    public static void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null ||
                !PAYMENT_METHODS.contains(paymentMethod.trim().toLowerCase())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid payment method: " + paymentMethod
            );
        }
    }

    public static void validateDiscountType(String discountType) {
        if (discountType == null ||
                !DISCOUNT_TYPES.contains(discountType.trim().toLowerCase())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid discount type: " + discountType
            );
        }
    }

    public static void validateNotificationType(String notificationType) {
        if (notificationType == null ||
                !NOTIFICATION_TYPES.contains(notificationType.trim().toLowerCase())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid notification type: " + notificationType
            );
        }
    }

    public static String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}