package com.example.javaexam.dtos;

import com.example.javaexam.models.MeterReading;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Meter reading view returned by the API. */
public record MeterReadingResponse(
        Long id,
        Long meterId,
        String meterNumber,
        BigDecimal previousReading,
        BigDecimal currentReading,
        BigDecimal consumption,
        LocalDate readingDate,
        int readingYear,
        int readingMonth,
        LocalDateTime createdAt
) {
    public static MeterReadingResponse from(MeterReading r) {
        return new MeterReadingResponse(
                r.getId(), r.getMeter().getId(), r.getMeter().getMeterNumber(),
                r.getPreviousReading(), r.getCurrentReading(), r.getConsumption(),
                r.getReadingDate(), r.getReadingYear(), r.getReadingMonth(), r.getCreatedAt());
    }
}
