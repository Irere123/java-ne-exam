package com.example.javaexam.security.dtos;

import jakarta.validation.constraints.NotBlank;

/** Payload for {@code POST /api/auth/logout} (revokes the given refresh token). */
public record LogoutRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
