package com.example.javaexam.controllers;

import com.example.javaexam.dtos.TariffRequest;
import com.example.javaexam.dtos.TariffResponse;
import com.example.javaexam.models.enums.MeterType;
import com.example.javaexam.services.TariffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Tariff, tax and penalty configuration (Task 4). JWT required. */
@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@Tag(name = "Tariffs", description = "Configure versioned tariffs, service charges, VAT and penalties (Task 4)")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Configure a new tariff version (ADMIN). "
            + "Applies only to billing cycles on/after its effective date.")
    public TariffResponse create(@Valid @RequestBody TariffRequest request) {
        return tariffService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List tariffs, optionally filtered by meter type (ADMIN/FINANCE)")
    public List<TariffResponse> list(@RequestParam(required = false) MeterType meterType) {
        return meterType == null ? tariffService.list() : tariffService.listByType(meterType);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Get a tariff by id (ADMIN/FINANCE)")
    public TariffResponse get(@PathVariable Long id) {
        return tariffService.get(id);
    }
}
