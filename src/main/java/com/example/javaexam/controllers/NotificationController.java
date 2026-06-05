package com.example.javaexam.controllers;

import com.example.javaexam.dtos.NotificationResponse;
import com.example.javaexam.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Notifications produced by the database routines. JWT required. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "View customer notifications generated automatically by database triggers")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List notifications, optionally filtered by customer (ADMIN/FINANCE)")
    public List<NotificationResponse> list(@RequestParam(required = false) Long customerId) {
        return customerId == null ? notificationService.list() : notificationService.listByCustomer(customerId);
    }
}
