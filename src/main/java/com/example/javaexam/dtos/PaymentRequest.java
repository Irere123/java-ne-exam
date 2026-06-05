package com.example.javaexam.dtos;

import com.example.javaexam.models.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload to record a payment against a bill (Task 5). */
public record PaymentRequest(

        @NotBlank(message = "Bill reference is required")
        @Schema(example = "BILL-202606-0001", description = "The bill_number to pay")
        String billNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
        @Schema(example = "5000.00")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        @Schema(example = "MOBILE_MONEY")
        PaymentMethod method,

        @NotNull(message = "Payment date is required")
        @PastOrPresent(message = "Payment date cannot be in the future")
        @Schema(example = "2026-06-20")
        LocalDate paymentDate
) {}
