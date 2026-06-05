package com.example.javaexam.repositories;

import com.example.javaexam.models.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNationalId(String nationalId);

    Optional<Customer> findByEmail(String email);

    /** The customer profile linked to a self-service login account, if any. */
    Optional<Customer> findByUserId(Long userId);

    boolean existsByNationalId(String nationalId);

    boolean existsByEmail(String email);
}
