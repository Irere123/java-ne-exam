package com.example.javaexam.repositories;

import com.example.javaexam.models.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPaymentReference(String paymentReference);

    List<Payment> findByBillIdOrderByCreatedAtDesc(Long billId);

    List<Payment> findByBillCustomerIdOrderByCreatedAtDesc(Long customerId);
}
