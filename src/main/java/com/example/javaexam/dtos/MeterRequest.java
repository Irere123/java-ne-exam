package com.example.javaexam.dtos;

import com.example.javaexam.models.enums.MeterType;
import com.example.javaexam.utils.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/** Payload to register a meter for a customer (Task 2). */
public record MeterRequest(

        @NotNull(message = "Customer id is required")
        @Positive(message = "Customer id must be positive")
        @Schema(example = "1")
        Long customerId,

        @Pattern(regexp = ValidationPatterns.METER_NUMBER,
                message = "Meter number must be 4-20 uppercase letters, digits or hyphens")
        @Schema(example = "WTR-000123")
        String meterNumber,

        @NotNull(message = "Meter type is required")
        @Schema(example = "WATER")
        MeterType meterType,

        @NotNull(message = "Installation date is required")
        @PastOrPresent(message = "Installation date cannot be in the future")
        @Schema(example = "2026-01-15")
        LocalDate installationDate
) {}
