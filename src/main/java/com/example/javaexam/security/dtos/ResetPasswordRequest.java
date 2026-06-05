package com.example.javaexam.security.dtos;

import com.example.javaexam.utils.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Payload for {@code POST /api/auth/reset-password}. */
public record ResetPasswordRequest(

        @NotBlank(message = "Token is required")
        @Pattern(regexp = ValidationPatterns.UUID, message = "Token must be a valid UUID")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        @Pattern(regexp = ValidationPatterns.STRONG_PASSWORD,
                message = "Password must include uppercase, lowercase, number, special character, and no spaces")
        String newPassword
) {}
