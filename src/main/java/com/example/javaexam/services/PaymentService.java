package com.example.javaexam.services;

import com.example.javaexam.dtos.PaymentRequest;
import com.example.javaexam.dtos.PaymentResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Bill;
import com.example.javaexam.models.Payment;
import com.example.javaexam.models.enums.BillStatus;
import com.example.javaexam.repositories.BillRepository;
import com.example.javaexam.repositories.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment processing (Task 5). Supports partial and full payments, keeps the
 * bill's outstanding balance in sync, and marks the bill PAID when the balance
 * reaches zero — which fires the DB trigger that notifies the customer (Task 6).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    @Transactional
    public PaymentResponse record(PaymentRequest request, Long recordedBy) {
        Bill bill = billRepository.findByBillNumber(request.billNumber().trim())
                .orElseThrow(() -> ApiException.notFound("Bill not found: " + request.billNumber()));

        if (bill.getStatus() == BillStatus.PENDING) {
            throw ApiException.badRequest("Bill " + bill.getBillNumber() + " must be approved before payment");
        }
        if (bill.getStatus() == BillStatus.PAID) {
            throw ApiException.badRequest("Bill " + bill.getBillNumber() + " is already fully paid");
        }
        if (request.amount().compareTo(bill.getOutstandingBalance()) > 0) {
            throw ApiException.badRequest("Amount exceeds the outstanding balance of "
                    + bill.getOutstandingBalance() + " FRW");
        }

        Payment payment = Payment.builder()
                .paymentReference(generatePaymentReference())
                .bill(bill)
                .amount(request.amount())
                .method(request.method())
                .paymentDate(request.paymentDate())
                .recordedBy(recordedBy)
                .build();
        paymentRepository.save(payment);

        BigDecimal amountPaid = bill.getAmountPaid().add(request.amount());
        BigDecimal outstanding = bill.getTotalAmount().subtract(amountPaid);
        bill.setAmountPaid(amountPaid);
        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
            bill.setOutstandingBalance(BigDecimal.ZERO);
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setOutstandingBalance(outstanding);
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }
        billRepository.save(bill);

        log.info("Recorded payment {} of {} on bill {} -> {} (outstanding {})",
                payment.getPaymentReference(), request.amount(), bill.getBillNumber(),
                bill.getStatus(), bill.getOutstandingBalance());
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listByBill(Long billId) {
        if (!billRepository.existsById(billId)) {
            throw ApiException.notFound("Bill not found: " + billId);
        }
        return paymentRepository.findByBillIdOrderByCreatedAtDesc(billId).stream()
                .map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listForCustomerEmail(String email) {
        return paymentRepository.findByBillCustomerEmailOrderByCreatedAtDesc(email.trim().toLowerCase()).stream()
                .map(PaymentResponse::from).toList();
    }

    private String generatePaymentReference() {
        int n = 1;
        String reference;
        do {
            reference = "PMT-" + String.format("%08d", n++);
        } while (paymentRepository.existsByPaymentReference(reference));
        return reference;
    }
}
