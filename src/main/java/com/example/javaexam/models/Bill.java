package com.example.javaexam.models;

import com.example.javaexam.models.enums.BillStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * A monthly postpaid bill generated from a meter reading and the applicable
 * tariff (Tasks 5 &amp; 6).
 *
 * <p>Inserting a bill fires the {@code trg_bill_after_insert} trigger, which
 * writes a BILL_GENERATED notification. When a payment drives
 * {@link #outstandingBalance} to zero the status becomes {@code PAID}, firing
 * {@code trg_bill_after_update}, which writes a PAYMENT_COMPLETED notification.
 */
@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", nullable = false, unique = true, length = 40)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reading_id", nullable = false)
    private MeterReading reading;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "billing_year", nullable = false)
    private int billingYear;

    @Column(name = "billing_month", nullable = false)
    private int billingMonth;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal consumption;

    @Column(name = "consumption_charge", nullable = false, precision = 14, scale = 2)
    private BigDecimal consumptionCharge;

    @Column(name = "service_charge", nullable = false, precision = 14, scale = 2)
    private BigDecimal serviceCharge;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "penalty_amount", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "outstanding_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "penalty_applied", nullable = false)
    @Builder.Default
    private boolean penaltyApplied = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private BillStatus status = BillStatus.PENDING;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
