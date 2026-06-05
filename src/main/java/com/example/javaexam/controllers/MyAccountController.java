package com.example.javaexam.controllers;

import com.example.javaexam.dtos.BillResponse;
import com.example.javaexam.dtos.NotificationResponse;
import com.example.javaexam.dtos.PaymentResponse;
import com.example.javaexam.models.Customer;
import com.example.javaexam.models.User;
import com.example.javaexam.services.BillService;
import com.example.javaexam.services.CustomerService;
import com.example.javaexam.services.NotificationService;
import com.example.javaexam.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Customer self-service portal. The signed-in account is resolved to its linked
 * customer profile (via {@code customers.user_id}), giving read-only access to
 * that customer's own bills, payments and notifications. Returns 404 if the
 * account has no customer profile linked yet.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "My Account (Customer)", description = "A customer's own bills, payments and notifications")
public class MyAccountController {

    private final CustomerService customerService;
    private final BillService billService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    @GetMapping("/bills")
    @Operation(summary = "List the signed-in customer's bills (CUSTOMER)")
    public List<BillResponse> myBills(@AuthenticationPrincipal User user) {
        Customer customer = customerService.getEntityByUser(user.getId());
        return billService.listByCustomer(customer.getId());
    }

    @GetMapping("/payments")
    @Operation(summary = "List the signed-in customer's payment history (CUSTOMER)")
    public List<PaymentResponse> myPayments(@AuthenticationPrincipal User user) {
        Customer customer = customerService.getEntityByUser(user.getId());
        return paymentService.listByCustomer(customer.getId());
    }

    @GetMapping("/notifications")
    @Operation(summary = "List the signed-in customer's notifications (CUSTOMER)")
    public List<NotificationResponse> myNotifications(@AuthenticationPrincipal User user) {
        Customer customer = customerService.getEntityByUser(user.getId());
        return notificationService.listByCustomer(customer.getId());
    }
}
