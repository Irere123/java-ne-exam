package com.example.javaexam.services;

import com.example.javaexam.dtos.TariffRequest;
import com.example.javaexam.dtos.TariffResponse;
import com.example.javaexam.dtos.TariffTierRequest;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Tariff;
import com.example.javaexam.models.TariffTier;
import com.example.javaexam.models.enums.MeterType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.javaexam.repositories.TariffRepository;

/**
 * Tariff configuration. Tariffs are versioned per meter type: each new
 * configuration becomes the next version and only applies to billing cycles on
 * or after its {@code effectiveFrom} date (see {@code BillService}).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {

    private final TariffRepository tariffRepository;

    @Transactional
    public TariffResponse create(TariffRequest request) {
        validateTiers(request.tiers());

        // Versions are ordered desc, so the first entry is the current (latest) one.
        List<Tariff> existingVersions = tariffRepository.findByMeterTypeOrderByVersionDesc(request.meterType());
        if (!existingVersions.isEmpty()) {
            LocalDate latestEffective = existingVersions.get(0).getEffectiveFrom();
            if (!request.effectiveFrom().isAfter(latestEffective)) {
                throw ApiException.badRequest("Effective-from must be after the current " + request.meterType()
                        + " tariff's effective date (" + latestEffective + ")");
            }
        }
        int nextVersion = existingVersions.isEmpty() ? 1 : existingVersions.get(0).getVersion() + 1;

        Tariff tariff = Tariff.builder()
                .meterType(request.meterType())
                .version(nextVersion)
                .serviceCharge(request.serviceCharge())
                .vatRate(request.vatRate())
                .penaltyRate(request.penaltyRate())
                .effectiveFrom(request.effectiveFrom())
                .description(request.description())
                .build();

        request.tiers().forEach(t -> tariff.addTier(TariffTier.builder()
                .minUnits(t.minUnits())
                .maxUnits(t.maxUnits())
                .ratePerUnit(t.ratePerUnit())
                .build()));

        tariffRepository.save(tariff);
        log.info("Configured {} tariff version {} effective {}",
                request.meterType(), nextVersion, request.effectiveFrom());
        return TariffResponse.from(tariff);
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> list() {
        return tariffRepository.findAll().stream().map(TariffResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> listByType(MeterType meterType) {
        return tariffRepository.findByMeterTypeOrderByVersionDesc(meterType).stream()
                .map(TariffResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TariffResponse get(Long id) {
        return TariffResponse.from(tariffRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Tariff not found: " + id)));
    }

    /**
     * Validates the structure of a tier-based (or flat) tariff so consumption is
     * priced unambiguously: the bands must start at zero, each band's max must
     * exceed its min, the bands must be contiguous (no gaps or overlaps), and
     * only the highest band may be open-ended (null max).
     */
    private void validateTiers(List<TariffTierRequest> tiers) {
        List<TariffTierRequest> ordered = tiers.stream()
                .sorted(Comparator.comparing(TariffTierRequest::minUnits))
                .toList();

        if (ordered.get(0).minUnits().compareTo(BigDecimal.ZERO) != 0) {
            throw ApiException.badRequest("The first consumption tier must start at 0 units");
        }

        for (int i = 0; i < ordered.size(); i++) {
            TariffTierRequest tier = ordered.get(i);
            boolean isLast = i == ordered.size() - 1;

            if (tier.maxUnits() == null) {
                if (!isLast) {
                    throw ApiException.badRequest("Only the highest consumption tier may be open-ended");
                }
                continue; // open-ended top tier: nothing further to check
            }
            if (tier.maxUnits().compareTo(tier.minUnits()) <= 0) {
                throw ApiException.badRequest("A tier's max units (" + tier.maxUnits()
                        + ") must be greater than its min units (" + tier.minUnits() + ")");
            }
            if (!isLast && tier.maxUnits().compareTo(ordered.get(i + 1).minUnits()) != 0) {
                throw ApiException.badRequest("Consumption tiers must be contiguous: a tier ending at "
                        + tier.maxUnits() + " must be followed by one starting at " + tier.maxUnits());
            }
        }
    }
}
