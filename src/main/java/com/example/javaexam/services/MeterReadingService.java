package com.example.javaexam.services;

import com.example.javaexam.dtos.MeterReadingRequest;
import com.example.javaexam.dtos.MeterReadingResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Meter;
import com.example.javaexam.models.MeterReading;
import com.example.javaexam.repositories.MeterReadingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Meter reading capture (Task 3). Business rules enforced here:
 * <ul>
 *   <li>the meter must be active;</li>
 *   <li>the current reading must be greater than the previous reading;</li>
 *   <li>at most one reading per meter per month/year.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeterReadingService {

    private final MeterReadingRepository readingRepository;
    private final MeterService meterService;

    @Transactional
    public MeterReadingResponse capture(MeterReadingRequest request, Long recordedBy) {
        Meter meter = meterService.getEntity(request.meterId());

        if (!meter.isActive()) {
            throw ApiException.badRequest("Cannot record a reading for an inactive meter");
        }
        if (request.currentReading().compareTo(request.previousReading()) <= 0) {
            throw ApiException.badRequest("Current reading must be greater than the previous reading");
        }

        int year = request.readingDate().getYear();
        int month = request.readingDate().getMonthValue();
        if (readingRepository.existsByMeterIdAndReadingYearAndReadingMonth(meter.getId(), year, month)) {
            throw ApiException.conflict(
                    "A reading for meter " + meter.getMeterNumber() + " already exists for " + month + "/" + year);
        }

        MeterReading reading = MeterReading.builder()
                .meter(meter)
                .previousReading(request.previousReading())
                .currentReading(request.currentReading())
                .consumption(request.currentReading().subtract(request.previousReading()))
                .readingDate(request.readingDate())
                .readingYear(year)
                .readingMonth(month)
                .recordedBy(recordedBy)
                .build();
        readingRepository.save(reading);

        log.info("Captured reading for meter {} ({}/{}), consumption {}",
                meter.getMeterNumber(), month, year, reading.getConsumption());
        return MeterReadingResponse.from(reading);
    }

    @Transactional(readOnly = true)
    public List<MeterReadingResponse> list() {
        return readingRepository.findAll().stream().map(MeterReadingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MeterReadingResponse> listByMeter(Long meterId) {
        meterService.getEntity(meterId); // 404 if the meter does not exist
        return readingRepository.findByMeterIdOrderByReadingYearDescReadingMonthDesc(meterId).stream()
                .map(MeterReadingResponse::from).toList();
    }

    public MeterReading getEntity(Long id) {
        return readingRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Meter reading not found: " + id));
    }
}
