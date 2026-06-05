package com.example.javaexam.controllers;

import com.example.javaexam.dtos.PaymentRequest;
import com.example.javaexam.dtos.PaymentResponse;
import com.example.javaexam.models.User;
import com.example.javaexam.services.PaymentService;
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

/** Payment processing (Task 5). JWT required. */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Record bill payments; partial and full (Task 5)")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    @Operation(summary = "Record a payment against a bill (FINANCE/ADMIN). "
            + "Updates the outstanding balance and marks the bill PAID at zero.")
    public PaymentResponse record(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User user) {
        return paymentService.record(request, user.getId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List the payments recorded against a bill (ADMIN/FINANCE)")
    public List<PaymentResponse> listByBill(@RequestParam Long billId) {
        return paymentService.listByBill(billId);
    }
}
