package com.example.javaexam.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload for an operator capturing a meter reading (Task 3). */
public record MeterReadingRequest(

        @NotNull(message = "Meter id is required")
        @Positive(message = "Meter id must be positive")
        @Schema(example = "1")
        Long meterId,

        @NotNull(message = "Previous reading is required")
        @DecimalMin(value = "0.0", message = "Previous reading cannot be negative")
        @Schema(example = "1200.000")
        BigDecimal previousReading,

        @NotNull(message = "Current reading is required")
        @DecimalMin(value = "0.0", message = "Current reading cannot be negative")
        @Schema(example = "1320.500")
        BigDecimal currentReading,

        @NotNull(message = "Reading date is required")
        @PastOrPresent(message = "Reading date cannot be in the future")
        @Schema(example = "2026-06-01")
        LocalDate readingDate
) {}
