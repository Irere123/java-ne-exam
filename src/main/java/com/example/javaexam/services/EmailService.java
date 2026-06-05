package com.example.javaexam.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails over SMTP. The {@link JavaMailSender} bean is
 * auto-configured from the {@code spring.mail.*} properties.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public void sendVerificationEmail(String to, String firstName, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Verify your email address");
        message.setText("""
                Hi %s,

                Thanks for registering. Please confirm your email address by opening the link below:

                %s

                If you did not create this account, you can safely ignore this email.
                """.formatted(firstName, verificationUrl));

        mailSender.send(message);
        log.info("Verification email sent to {}", to);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("""
                Hi %s,

                We received a request to reset your password. Use the link below to choose a new one:

                %s

                This link expires shortly. If you did not request a password reset, you can safely
                ignore this email and your password will remain unchanged.
                """.formatted(firstName, resetUrl));

        mailSender.send(message);
        log.info("Password-reset email sent to {}", to);
    }
}
