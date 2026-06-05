package com.example.javaexam.dtos;

import com.example.javaexam.models.Bill;
import com.example.javaexam.models.enums.BillStatus;
import com.example.javaexam.models.enums.MeterType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Bill view returned by the API. */
public record BillResponse(
        Long id,
        String billNumber,
        Long customerId,
        String customerName,
        Long meterId,
        String meterNumber,
        MeterType meterType,
        int billingYear,
        int billingMonth,
        BigDecimal consumption,
        BigDecimal consumptionCharge,
        BigDecimal serviceCharge,
        BigDecimal taxAmount,
        BigDecimal penaltyAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal outstandingBalance,
        BillStatus status,
        LocalDate dueDate,
        LocalDateTime createdAt
) {
    public static BillResponse from(Bill b) {
        return new BillResponse(
                b.getId(), b.getBillNumber(), b.getCustomer().getId(), b.getCustomer().getFullName(),
                b.getMeter().getId(), b.getMeter().getMeterNumber(), b.getMeter().getMeterType(),
                b.getBillingYear(), b.getBillingMonth(), b.getConsumption(), b.getConsumptionCharge(),
                b.getServiceCharge(), b.getTaxAmount(), b.getPenaltyAmount(), b.getTotalAmount(),
                b.getAmountPaid(), b.getOutstandingBalance(), b.getStatus(), b.getDueDate(),
                b.getCreatedAt());
    }
}
