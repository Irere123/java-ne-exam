package com.example.javaexam.dtos;

import com.example.javaexam.models.enums.Role;
import com.example.javaexam.utils.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Admin payload to create a staff or customer account with a specific role.
 * The account is created already enabled, with no email verification required.
 */
public record AdminCreateUserRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        @Pattern(regexp = ValidationPatterns.HUMAN_NAME,
                message = "First name must contain letters only, with optional spaces, apostrophes, or hyphens")
        @Schema(example = "Claudine")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        @Pattern(regexp = ValidationPatterns.HUMAN_NAME,
                message = "Last name must contain letters only, with optional spaces, apostrophes, or hyphens")
        @Schema(example = "Uwase")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid address")
        @Size(max = 255, message = "Email must be at most 255 characters")
        @Schema(example = "operator@wasac.rw")
        String email,

        @Pattern(regexp = ValidationPatterns.COUNTRY_CODE,
                message = "Country code must be a '+' followed by 1-4 digits")
        @Schema(example = "+250", description = "Dialing code; defaults to +250 (Rwanda) when omitted")
        String countryCode,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = ValidationPatterns.PHONE_NATIONAL,
                message = "Phone number must be 6-12 digits, without the country code")
        @Schema(example = "788654321")
        String phoneNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        @Pattern(regexp = ValidationPatterns.STRONG_PASSWORD,
                message = "Password must include uppercase, lowercase, number, special character, and no spaces")
        @Schema(example = "ExamPass1!")
        String password,

        @NotNull(message = "Role is required")
        @Schema(example = "OPERATOR")
        Role role
) {}
