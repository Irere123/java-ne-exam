package com.example.javaexam.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload for {@code POST /api/auth/logout} (revokes the given refresh token). */
public record LogoutRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
