package com.example.javaexam.security.dtos;

import jakarta.validation.constraints.NotBlank;

/** Payload for {@code POST /api/auth/refresh}. */
public record RefreshRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
