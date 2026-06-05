package com.example.javaexam.repositories;

import com.example.javaexam.models.MeterReading;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    boolean existsByMeterIdAndReadingYearAndReadingMonth(Long meterId, int readingYear, int readingMonth);

    List<MeterReading> findByMeterIdOrderByReadingYearDescReadingMonthDesc(Long meterId);
}
