package com.example.javaexam.dtos;

import com.example.javaexam.utils.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Payload to create or update a customer (Task 2). */
public record CustomerRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        @Schema(example = "Jean Bosco Habimana")
        String fullName,

        @NotBlank(message = "National ID is required")
        @Pattern(regexp = ValidationPatterns.NATIONAL_ID, message = "National ID must be exactly 16 digits")
        @Schema(example = "1199870012345678")
        String nationalId,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid address")
        @Size(max = 255, message = "Email must be at most 255 characters")
        @Schema(example = "jean.habimana@example.com")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = ValidationPatterns.PHONE,
                message = "Phone number must be 10-15 digits, with an optional leading '+'")
        @Schema(example = "+250788123456")
        String phoneNumber,

        @Size(max = 255, message = "Address must be at most 255 characters")
        @Schema(example = "KG 11 Ave, Kigali")
        String address
) {}
