package com.example.javaexam.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * One consumption band of a tariff (Task 4). Units in {@code [minUnits, maxUnits)}
 * are charged at {@code ratePerUnit}. Leave {@code maxUnits} null for the
 * unbounded top tier. A flat tariff is a single tier with min 0 and null max.
 */
public record TariffTierRequest(

        @NotNull(message = "Tier minimum units is required")
        @DecimalMin(value = "0.0", message = "Minimum units cannot be negative")
        @Schema(example = "0")
        BigDecimal minUnits,

        @DecimalMin(value = "0.0", message = "Maximum units cannot be negative")
        @Schema(example = "100", nullable = true)
        BigDecimal maxUnits,

        @NotNull(message = "Rate per unit is required")
        @DecimalMin(value = "0.0", message = "Rate per unit cannot be negative")
        @Schema(example = "323.00")
        BigDecimal ratePerUnit
) {}
