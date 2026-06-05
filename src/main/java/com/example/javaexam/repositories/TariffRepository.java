package com.example.javaexam.repositories;

import com.example.javaexam.models.Tariff;
import com.example.javaexam.models.enums.MeterType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    List<Tariff> findByMeterTypeOrderByVersionDesc(MeterType meterType);

    /** Highest existing version for a meter type, or 0 if none — used to assign the next version. */
    @Query("SELECT COALESCE(MAX(t.version), 0) FROM Tariff t WHERE t.meterType = :meterType")
    int findMaxVersion(@Param("meterType") MeterType meterType);

    /**
     * The tariff that applies to a billing period: the latest version whose
     * {@code effectiveFrom} is on or before the given date. Enforces the rule
     * that newer tariffs only apply to future billing cycles.
     */
    Optional<Tariff> findFirstByMeterTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDescVersionDesc(
            MeterType meterType, LocalDate date);
}
