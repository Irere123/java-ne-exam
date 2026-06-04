package com.example.javaexam.dto;

import com.example.javaexam.model.Role;

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
