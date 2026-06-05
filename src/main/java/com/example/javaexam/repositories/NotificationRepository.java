package com.example.javaexam.repositories;

import com.example.javaexam.models.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    /** Undelivered notifications, oldest first, capped so each dispatch run is bounded. */
    List<Notification> findTop100ByEmailSentFalseOrderByCreatedAtAsc();
}
