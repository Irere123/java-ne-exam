package com.example.javaexam.controllers;

import com.example.javaexam.dtos.BillResponse;
import com.example.javaexam.models.domains.ApiResponse;
import com.example.javaexam.services.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Bill generation, approval and late-penalty processing. JWT required. */
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bills", description = "Generate bills from meter readings, approve them for payment, and apply penalties to overdue bills")
public class BillController {

    private final BillService billService;

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    @Operation(summary = "Generate a bill from a meter reading (FINANCE/ADMIN). "
            + "Inserting the bill triggers a BILL_GENERATED notification.")
    public BillResponse generate(@RequestParam @Positive(message = "readingId must be a positive number") Long readingId) {
        return billService.generate(readingId);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    @Operation(summary = "Approve a pending bill, making it payable (FINANCE/ADMIN)")
    public BillResponse approve(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        return billService.approve(id);
    }

    @PostMapping("/apply-penalties")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    @Operation(summary = "Apply late penalties to overdue bills via the stored procedure (FINANCE/ADMIN)")
    public ApiResponse applyPenalties() {
        billService.applyLatePenalties();
        return new ApiResponse("Late penalties applied to overdue bills.");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List bills, optionally filtered by customer (ADMIN/FINANCE)")
    public List<BillResponse> list(@RequestParam(required = false) Long customerId) {
        return customerId == null ? billService.list() : billService.listByCustomer(customerId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Get a bill by id (ADMIN/FINANCE)")
    public BillResponse get(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        return billService.get(id);
    }
}
