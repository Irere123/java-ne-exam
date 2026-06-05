package com.example.javaexam.services;

import com.example.javaexam.models.Notification;
import com.example.javaexam.repositories.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Emails the customer notifications that the database triggers write on bill
 * generation and full payment.
 *
 * <p>Because those rows are created at the database level (outside the
 * application), an outbox-style poller is the natural way to react to them: on a
 * fixed schedule it picks up notifications that have not been emailed yet, sends
 * each one, and flips {@code email_sent} only once its email succeeds — so a
 * transient SMTP failure is harmlessly retried on the next run, and every
 * notification is delivered exactly once.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Scheduled(initialDelayString = "${app.notifications.dispatch-interval-ms}",
            fixedDelayString = "${app.notifications.dispatch-interval-ms}")
    @Transactional
    public void dispatchPendingEmails() {
        List<Notification> pending = notificationRepository.findTop100ByEmailSentFalseOrderByCreatedAtAsc();
        if (pending.isEmpty()) {
            return;
        }

        int sent = 0;
        for (Notification notification : pending) {
            String recipient = notification.getCustomer().getEmail();
            boolean delivered = emailService.sendNotificationEmail(
                    recipient, notification.getType(), notification.getMessage());
            if (delivered) {
                notification.setEmailSent(true); // managed entity: flushed on commit
                sent++;
            }
        }

        log.info("Notification dispatch: emailed {} of {} pending notification(s)", sent, pending.size());
    }
}
