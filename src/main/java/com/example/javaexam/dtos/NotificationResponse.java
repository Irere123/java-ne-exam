package com.example.javaexam.dtos;

import com.example.javaexam.models.Notification;
import com.example.javaexam.models.enums.NotificationType;
import java.time.LocalDateTime;

/** Notification view returned by the API. */
public record NotificationResponse(
        Long id,
        Long customerId,
        Long billId,
        NotificationType type,
        String message,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getCustomer().getId(),
                n.getBill() != null ? n.getBill().getId() : null,
                n.getType(),
                n.getMessage(),
                n.getCreatedAt());
    }
}
