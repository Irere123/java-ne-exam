package com.example.javaexam.repositories;

import com.example.javaexam.models.Meter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeterRepository extends JpaRepository<Meter, Long> {

    Optional<Meter> findByMeterNumber(String meterNumber);

    boolean existsByMeterNumber(String meterNumber);

    List<Meter> findByCustomerId(Long customerId);
}
