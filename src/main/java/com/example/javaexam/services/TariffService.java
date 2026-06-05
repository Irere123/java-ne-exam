package com.example.javaexam.services;

import com.example.javaexam.dtos.TariffRequest;
import com.example.javaexam.dtos.TariffResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Tariff;
import com.example.javaexam.models.TariffTier;
import com.example.javaexam.models.enums.MeterType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.javaexam.repositories.TariffRepository;

/**
 * Tariff configuration (Task 4). Tariffs are versioned per meter type: each new
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
        int nextVersion = tariffRepository.findMaxVersion(request.meterType()) + 1;

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
}
