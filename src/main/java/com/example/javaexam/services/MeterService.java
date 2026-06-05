package com.example.javaexam.services;

import com.example.javaexam.dtos.MeterRequest;
import com.example.javaexam.dtos.MeterResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Customer;
import com.example.javaexam.models.Meter;
import com.example.javaexam.models.enums.MeterType;
import com.example.javaexam.models.enums.Status;
import com.example.javaexam.repositories.MeterRepository;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Meter management: each customer may own one or more meters. */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeterService {

    private final MeterRepository meterRepository;
    private final CustomerService customerService;

    @Transactional
    public MeterResponse create(MeterRequest request) {
        Customer customer = customerService.getEntity(request.customerId());

        String meterNumber = (request.meterNumber() == null || request.meterNumber().isBlank())
                ? generateMeterNumber(request.meterType())
                : request.meterNumber().trim().toUpperCase();

        if (meterRepository.existsByMeterNumber(meterNumber)) {
            throw ApiException.conflict("A meter with number '" + meterNumber + "' already exists");
        }

        Meter meter = Meter.builder()
                .meterNumber(meterNumber)
                .meterType(request.meterType())
                .installationDate(request.installationDate())
                .status(Status.ACTIVE)
                .customer(customer)
                .build();
        meterRepository.save(meter);

        log.info("Registered {} meter {} for customer {}", request.meterType(), meterNumber, customer.getId());
        return MeterResponse.from(meter);
    }

    @Transactional
    public MeterResponse updateStatus(Long id, Status status) {
        Meter meter = getEntity(id);
        meter.setStatus(status);
        meterRepository.save(meter);
        log.info("Meter {} status set to {}", id, status);
        return MeterResponse.from(meter);
    }

    @Transactional(readOnly = true)
    public List<MeterResponse> list() {
        return meterRepository.findAll().stream().map(MeterResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MeterResponse> listByCustomer(Long customerId) {
        customerService.getEntity(customerId); // 404 if the customer does not exist
        return meterRepository.findByCustomerId(customerId).stream().map(MeterResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MeterResponse get(Long id) {
        return MeterResponse.from(getEntity(id));
    }

    public Meter getEntity(Long id) {
        return meterRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Meter not found: " + id));
    }

    private String generateMeterNumber(MeterType type) {
        String prefix = type == MeterType.WATER ? "WTR-" : "ELE-";
        String number;
        do {
            number = prefix + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        } while (meterRepository.existsByMeterNumber(number));
        return number;
    }
}
