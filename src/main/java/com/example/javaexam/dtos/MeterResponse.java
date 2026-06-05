package com.example.javaexam.dtos;

import com.example.javaexam.models.Meter;
import com.example.javaexam.models.enums.MeterType;
import com.example.javaexam.models.enums.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Meter view returned by the API (Task 2). */
public record MeterResponse(
        Long id,
        String meterNumber,
        MeterType meterType,
        LocalDate installationDate,
        Status status,
        Long customerId,
        String customerName,
        LocalDateTime createdAt
) {
    public static MeterResponse from(Meter m) {
        return new MeterResponse(
                m.getId(), m.getMeterNumber(), m.getMeterType(), m.getInstallationDate(),
                m.getStatus(), m.getCustomer().getId(), m.getCustomer().getFullName(), m.getCreatedAt());
    }
}
