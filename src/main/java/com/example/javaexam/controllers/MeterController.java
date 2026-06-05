package com.example.javaexam.controllers;

import com.example.javaexam.dtos.MeterRequest;
import com.example.javaexam.dtos.MeterResponse;
import com.example.javaexam.dtos.StatusRequest;
import com.example.javaexam.services.MeterService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Meter management. JWT required. */
@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
@Tag(name = "Meters", description = "Register meters to customers and manage their active status")
public class MeterController {

    private final MeterService meterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @Operation(summary = "Register a meter for a customer (ADMIN/OPERATOR). Meter number is generated if omitted.")
    public MeterResponse create(@Valid @RequestBody MeterRequest request) {
        return meterService.create(request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @Operation(summary = "Activate or deactivate a meter (ADMIN/OPERATOR)")
    public MeterResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return meterService.updateStatus(id, request.status());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @Operation(summary = "List all meters, optionally filtered by customer (ADMIN/OPERATOR/FINANCE)")
    public List<MeterResponse> list(@RequestParam(required = false) Long customerId) {
        return customerId == null ? meterService.list() : meterService.listByCustomer(customerId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @Operation(summary = "Get a meter by id (ADMIN/OPERATOR/FINANCE)")
    public MeterResponse get(@PathVariable Long id) {
        return meterService.get(id);
    }
}
