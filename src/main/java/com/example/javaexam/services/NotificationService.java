package com.example.javaexam.services;

import com.example.javaexam.dtos.NotificationResponse;
import com.example.javaexam.repositories.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read access to notifications. Notification rows are produced by
 * database triggers on bill generation and full payment, so this service only
 * exposes them — it never creates them.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> list() {
        return notificationRepository.findAll().stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listByCustomer(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(NotificationResponse::from).toList();
    }
}
