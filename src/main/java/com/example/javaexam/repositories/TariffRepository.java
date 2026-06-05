package com.example.javaexam.repositories;

import com.example.javaexam.models.Tariff;
import com.example.javaexam.models.enums.MeterType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    List<Tariff> findByMeterTypeOrderByVersionDesc(MeterType meterType);

    /**
     * The tariff that applies to a billing period: the latest version whose
     * {@code effectiveFrom} is on or before the given date. Enforces the rule
     * that newer tariffs only apply to future billing cycles.
     */
    Optional<Tariff> findFirstByMeterTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDescVersionDesc(
            MeterType meterType, LocalDate date);
}
