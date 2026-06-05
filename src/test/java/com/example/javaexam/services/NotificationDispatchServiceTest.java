package com.example.javaexam.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.javaexam.models.Customer;
import com.example.javaexam.models.Notification;
import com.example.javaexam.models.enums.NotificationType;
import com.example.javaexam.repositories.NotificationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for the outbox-style notification email dispatcher. */
@ExtendWith(MockitoExtension.class)
class NotificationDispatchServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationDispatchService service;

    private Notification pending(String email, NotificationType type, String message) {
        return Notification.builder()
                .customer(Customer.builder().email(email).build())
                .type(type)
                .message(message)
                .emailSent(false)
                .build();
    }

    @Test
    void marksNotificationSentWhenEmailSucceeds() {
        Notification n = pending("jean@example.com", NotificationType.BILL_GENERATED, "Dear Jean, ...");
        when(notificationRepository.findTop100ByEmailSentFalseOrderByCreatedAtAsc()).thenReturn(List.of(n));
        when(emailService.sendNotificationEmail(any(), any(), any())).thenReturn(true);

        service.dispatchPendingEmails();

        verify(emailService).sendNotificationEmail(
                eq("jean@example.com"), eq(NotificationType.BILL_GENERATED), eq("Dear Jean, ..."));
        assertThat(n.isEmailSent()).isTrue();
    }

    @Test
    void leavesNotificationUnsentWhenEmailFails() {
        Notification n = pending("jean@example.com", NotificationType.PAYMENT_COMPLETED, "Dear Jean, ...");
        when(notificationRepository.findTop100ByEmailSentFalseOrderByCreatedAtAsc()).thenReturn(List.of(n));
        when(emailService.sendNotificationEmail(any(), any(), any())).thenReturn(false);

        service.dispatchPendingEmails();

        assertThat(n.isEmailSent()).isFalse(); // stays pending, retried next run
    }

    @Test
    void doesNothingWhenNothingPending() {
        when(notificationRepository.findTop100ByEmailSentFalseOrderByCreatedAtAsc()).thenReturn(List.of());

        service.dispatchPendingEmails();

        verify(emailService, never()).sendNotificationEmail(any(), any(), any());
    }
}
