package com.example.javaexam.services;

import com.example.javaexam.dtos.BillResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Bill;
import com.example.javaexam.models.Customer;
import com.example.javaexam.models.Meter;
import com.example.javaexam.models.MeterReading;
import com.example.javaexam.models.Tariff;
import com.example.javaexam.models.TariffTier;
import com.example.javaexam.models.enums.BillStatus;
import com.example.javaexam.repositories.BillRepository;
import com.example.javaexam.repositories.TariffRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bill generation and lifecycle.
 *
 * <p>A bill is generated from a single meter reading using the tariff version in
 * effect for the reading's billing period. Inactive customers cannot be billed.
 * Inserting the bill fires a database trigger that records a BILL_GENERATED
 * notification. Late penalties are applied by the {@code
 * sp_apply_late_penalties} stored procedure (cursor-based).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {

    private final BillRepository billRepository;
    private final TariffRepository tariffRepository;
    private final MeterReadingService meterReadingService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public BillResponse generate(Long readingId) {
        MeterReading reading = meterReadingService.getEntity(readingId);

        if (billRepository.existsByReadingId(readingId)) {
            throw ApiException.conflict("A bill has already been generated for reading " + readingId);
        }

        Meter meter = reading.getMeter();
        Customer customer = meter.getCustomer();
        if (!customer.isActive()) {
            throw ApiException.badRequest("Inactive customers cannot receive bills");
        }

        int year = reading.getReadingYear();
        int month = reading.getReadingMonth();
        LocalDate periodStart = LocalDate.of(year, month, 1);

        Tariff tariff = tariffRepository
                .findFirstByMeterTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDescVersionDesc(
                        meter.getMeterType(), periodStart)
                .orElseThrow(() -> ApiException.badRequest(
                        "No " + meter.getMeterType() + " tariff is effective for " + month + "/" + year));

        BigDecimal consumption = reading.getConsumption();
        BigDecimal consumptionCharge = computeConsumptionCharge(tariff.getTiers(), consumption);
        BigDecimal serviceCharge = scale(tariff.getServiceCharge());
        BigDecimal subtotal = consumptionCharge.add(serviceCharge);
        BigDecimal taxAmount = scale(subtotal.multiply(tariff.getVatRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        BigDecimal totalAmount = subtotal.add(taxAmount);

        Bill bill = Bill.builder()
                .billNumber(generateBillNumber(year, month))
                .customer(customer)
                .meter(meter)
                .reading(reading)
                .tariff(tariff)
                .billingYear(year)
                .billingMonth(month)
                .consumption(consumption)
                .consumptionCharge(consumptionCharge)
                .serviceCharge(serviceCharge)
                .taxAmount(taxAmount)
                .penaltyAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .amountPaid(BigDecimal.ZERO)
                .outstandingBalance(totalAmount)
                .status(BillStatus.PENDING)
                .dueDate(periodStart.plusMonths(1).plusDays(14))
                .build();
        billRepository.save(bill);

        log.info("Generated bill {} for customer {} ({}/{}), total {}",
                bill.getBillNumber(), customer.getId(), month, year, totalAmount);
        return BillResponse.from(bill);
    }

    /** Approves a pending bill, making it payable. */
    @Transactional
    public BillResponse approve(Long id) {
        Bill bill = getEntity(id);
        if (bill.getStatus() != BillStatus.PENDING) {
            throw ApiException.badRequest("Only PENDING bills can be approved; bill is " + bill.getStatus());
        }
        bill.setStatus(BillStatus.APPROVED);
        billRepository.save(bill);
        log.info("Approved bill {}", bill.getBillNumber());
        return BillResponse.from(bill);
    }

    /**
     * Runs the cursor-based stored procedure that applies the configured late
     * penalty to every overdue, unpaid bill. PostgreSQL only.
     */
    @Transactional
    public void applyLatePenalties() {
        jdbcTemplate.execute("CALL sp_apply_late_penalties()");
        log.info("Applied late penalties via sp_apply_late_penalties()");
    }

    @Transactional(readOnly = true)
    public List<BillResponse> list() {
        return billRepository.findAll().stream().map(BillResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BillResponse get(Long id) {
        return BillResponse.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<BillResponse> listByCustomer(Long customerId) {
        return billRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(BillResponse::from).toList();
    }

    public Bill getEntity(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Bill not found: " + id));
    }

    /**
     * Charges {@code consumption} units against the tariff's tiers. Units within
     * {@code [minUnits, maxUnits)} are billed at that tier's rate; a null
     * {@code maxUnits} is the unbounded top tier.
     */
    private BigDecimal computeConsumptionCharge(List<TariffTier> tiers, BigDecimal consumption) {
        BigDecimal charge = BigDecimal.ZERO;
        List<TariffTier> ordered = tiers.stream()
                .sorted(Comparator.comparing(TariffTier::getMinUnits))
                .toList();

        for (TariffTier tier : ordered) {
            BigDecimal lower = tier.getMinUnits();
            if (consumption.compareTo(lower) <= 0) {
                continue;
            }
            BigDecimal upper = tier.getMaxUnits() == null ? consumption : tier.getMaxUnits().min(consumption);
            BigDecimal unitsInTier = upper.subtract(lower);
            if (unitsInTier.signum() > 0) {
                charge = charge.add(unitsInTier.multiply(tier.getRatePerUnit()));
            }
        }
        return scale(charge);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateBillNumber(int year, int month) {
        String prefix = "BILL-" + year + String.format("%02d", month) + "-";
        int n = 1;
        String number;
        do {
            number = prefix + String.format("%04d", n++);
        } while (billRepository.existsByBillNumber(number));
        return number;
    }
}
