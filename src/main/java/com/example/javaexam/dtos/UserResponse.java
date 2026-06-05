package com.example.javaexam.dtos;

import com.example.javaexam.models.User;
import com.example.javaexam.models.enums.Role;
import java.time.LocalDateTime;

/** User view returned by the admin user-management endpoints. */
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role,
        boolean enabled,
        LocalDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(),
                u.getPhoneNumber(), u.getRole(), u.isEnabled(), u.getCreatedAt());
    }
}
