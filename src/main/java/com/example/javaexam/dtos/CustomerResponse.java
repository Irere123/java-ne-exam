package com.example.javaexam.dtos;

import com.example.javaexam.models.Customer;
import com.example.javaexam.models.enums.Status;
import java.time.LocalDateTime;

/** Customer view returned by the API (Task 2). */
public record CustomerResponse(
        Long id,
        String fullName,
        String nationalId,
        String email,
        String phoneNumber,
        String address,
        Status status,
        LocalDateTime createdAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getFullName(), c.getNationalId(), c.getEmail(),
                c.getPhoneNumber(), c.getAddress(), c.getStatus(), c.getCreatedAt());
    }
}
