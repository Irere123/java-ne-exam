package com.example.javaexam.repositories;

import com.example.javaexam.models.Bill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    boolean existsByReadingId(Long readingId);

    boolean existsByBillNumber(String billNumber);

    List<Bill> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Bill> findByCustomerEmailOrderByCreatedAtDesc(String email);
}
