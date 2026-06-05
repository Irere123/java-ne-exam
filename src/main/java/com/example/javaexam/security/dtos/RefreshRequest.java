package com.example.javaexam.security.dtos;

import com.example.javaexam.utils.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Payload for {@code POST /api/auth/refresh}. */
public record RefreshRequest(

        @NotBlank(message = "Refresh token is required")
        @Size(max = 2048, message = "Refresh token is too long")
        @Pattern(regexp = ValidationPatterns.JWT, message = "Refresh token must be a valid JWT")
        String refreshToken
) {}
