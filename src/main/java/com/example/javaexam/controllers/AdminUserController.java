package com.example.javaexam.controllers;

import com.example.javaexam.dtos.AdminCreateUserRequest;
import com.example.javaexam.dtos.RoleUpdateRequest;
import com.example.javaexam.dtos.StatusRequest;
import com.example.javaexam.dtos.UserResponse;
import com.example.javaexam.services.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin user management (Task 1). Mapped under {@code /api/admin/**}, which
 * SecurityConfig already restricts to {@code ROLE_ADMIN}; the method-level
 * {@code @PreAuthorize} keeps the requirement explicit.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin / Users", description = "Create users and manage roles and status (ADMIN, Task 1)")
public class AdminUserController {

    private final UserAdminService userAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a user with a specific role (ADMIN)")
    public UserResponse create(@Valid @RequestBody AdminCreateUserRequest request) {
        return userAdminService.create(request);
    }

    @GetMapping
    @Operation(summary = "List all users (ADMIN)")
    public List<UserResponse> list() {
        return userAdminService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id (ADMIN)")
    public UserResponse get(@PathVariable Long id) {
        return userAdminService.get(id);
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change a user's role; revokes their existing sessions (ADMIN)")
    public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return userAdminService.updateRole(id, request.role());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate a user account (ADMIN)")
    public UserResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return userAdminService.updateStatus(id, request.status());
    }
}
