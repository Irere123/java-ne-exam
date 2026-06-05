package com.example.javaexam.dtos;

import com.example.javaexam.models.Payment;
import com.example.javaexam.models.enums.BillStatus;
import com.example.javaexam.models.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Payment view returned by the API, including the bill's resulting state (Task 5). */
public record PaymentResponse(
        Long id,
        String paymentReference,
        String billNumber,
        BigDecimal amount,
        PaymentMethod method,
        LocalDate paymentDate,
        BillStatus billStatus,
        BigDecimal billOutstandingBalance,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getPaymentReference(), p.getBill().getBillNumber(), p.getAmount(),
                p.getMethod(), p.getPaymentDate(), p.getBill().getStatus(),
                p.getBill().getOutstandingBalance(), p.getCreatedAt());
    }
}
