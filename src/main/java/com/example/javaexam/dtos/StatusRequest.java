package com.example.javaexam.dtos;

import com.example.javaexam.models.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Payload to activate/deactivate a customer, meter, or user account. */
public record StatusRequest(

        @NotNull(message = "Status is required")
        @Schema(example = "INACTIVE")
        Status status
) {}
