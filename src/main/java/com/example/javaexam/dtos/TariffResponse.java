package com.example.javaexam.dtos;

import com.example.javaexam.models.Tariff;
import com.example.javaexam.models.TariffTier;
import com.example.javaexam.models.enums.MeterType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Tariff view returned by the API. */
public record TariffResponse(
        Long id,
        MeterType meterType,
        int version,
        BigDecimal serviceCharge,
        BigDecimal vatRate,
        BigDecimal penaltyRate,
        LocalDate effectiveFrom,
        String description,
        List<TariffTierResponse> tiers,
        LocalDateTime createdAt
) {
    public static TariffResponse from(Tariff t) {
        List<TariffTierResponse> tiers = t.getTiers().stream()
                .map(TariffTierResponse::from)
                .toList();
        return new TariffResponse(
                t.getId(), t.getMeterType(), t.getVersion(), t.getServiceCharge(),
                t.getVatRate(), t.getPenaltyRate(), t.getEffectiveFrom(), t.getDescription(),
                tiers, t.getCreatedAt());
    }

    /** Nested tier view. */
    public record TariffTierResponse(
            Long id,
            BigDecimal minUnits,
            BigDecimal maxUnits,
            BigDecimal ratePerUnit
    ) {
        public static TariffTierResponse from(TariffTier tier) {
            return new TariffTierResponse(
                    tier.getId(), tier.getMinUnits(), tier.getMaxUnits(), tier.getRatePerUnit());
        }
    }
}
