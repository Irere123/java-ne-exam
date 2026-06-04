package com.example.javaexam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Payload for {@code POST /api/auth/resend-verification}. */
public record ResendVerificationRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid address")
        String email
) {}
