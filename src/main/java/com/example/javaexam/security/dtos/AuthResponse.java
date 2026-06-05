package com.example.javaexam.security.dtos;

import com.example.javaexam.models.enums.Role;

/** Token pair returned from login, refresh, and change-password. */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long accessExpiresInMs,
        long refreshExpiresInMs,
        String email,
        Role role
) {}
