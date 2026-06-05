package com.example.javaexam.dtos;

import com.example.javaexam.models.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Admin payload to change a user's role (Task 1: "manage users"). */
public record RoleUpdateRequest(

        @NotNull(message = "Role is required")
        @Schema(example = "FINANCE")
        Role role
) {}
