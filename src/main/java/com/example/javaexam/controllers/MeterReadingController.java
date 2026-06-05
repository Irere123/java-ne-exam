package com.example.javaexam.controllers;

import com.example.javaexam.dtos.MeterReadingRequest;
import com.example.javaexam.dtos.MeterReadingResponse;
import com.example.javaexam.models.User;
import com.example.javaexam.services.MeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Meter reading capture. JWT required. */
@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@Tag(name = "Meter Readings", description = "Capture monthly meter readings that bills are generated from")
public class MeterReadingController {

    private final MeterReadingService readingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    @Operation(summary = "Capture a meter reading (OPERATOR/ADMIN). "
            + "Current must exceed previous; one reading per meter per month.")
    public MeterReadingResponse capture(
            @Valid @RequestBody MeterReadingRequest request,
            @AuthenticationPrincipal User user) {
        return readingService.capture(request, user.getId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @Operation(summary = "List meter readings, optionally filtered by meter (ADMIN/OPERATOR/FINANCE)")
    public List<MeterReadingResponse> list(@RequestParam(required = false) Long meterId) {
        return meterId == null ? readingService.list() : readingService.listByMeter(meterId);
    }
}
