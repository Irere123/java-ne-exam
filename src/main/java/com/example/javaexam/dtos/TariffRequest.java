package com.example.javaexam.dtos;

import com.example.javaexam.models.enums.MeterType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Payload to configure a new tariff version. The version number is
 * assigned automatically (latest + 1) for the meter type.
 */
public record TariffRequest(

        @NotNull(message = "Meter type is required")
        @Schema(example = "WATER")
        MeterType meterType,

        @NotNull(message = "Service charge is required")
        @DecimalMin(value = "0.0", message = "Service charge cannot be negative")
        @Schema(example = "2000.00")
        BigDecimal serviceCharge,

        @NotNull(message = "VAT rate is required")
        @DecimalMin(value = "0.0", message = "VAT rate cannot be negative")
        @DecimalMax(value = "100.0", message = "VAT rate cannot exceed 100")
        @Schema(example = "18.00")
        BigDecimal vatRate,

        @NotNull(message = "Penalty rate is required")
        @DecimalMin(value = "0.0", message = "Penalty rate cannot be negative")
        @DecimalMax(value = "100.0", message = "Penalty rate cannot exceed 100")
        @Schema(example = "5.00")
        BigDecimal penaltyRate,

        @NotNull(message = "Effective-from date is required")
        @Schema(example = "2026-07-01")
        LocalDate effectiveFrom,

        @Size(max = 255, message = "Description must be at most 255 characters")
        @Schema(example = "WASAC water tariff 2026 H2")
        String description,

        @NotEmpty(message = "At least one consumption tier is required")
        @Valid
        List<TariffTierRequest> tiers
) {}
