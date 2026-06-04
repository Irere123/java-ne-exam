package com.example.javaexam.dto;

import com.example.javaexam.model.Role;

/** Returned from {@code POST /api/auth/login} on success. */
public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        String email,
        Role role
) {}
