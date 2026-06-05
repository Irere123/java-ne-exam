package com.example.javaexam.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * A monthly meter reading. At most one reading per meter per
 * month/year, enforced by {@code uq_meter_readings_period}.
 */
@Entity
@Table(
        name = "meter_readings",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_meter_readings_period",
                columnNames = {"meter_id", "reading_year", "reading_month"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(name = "previous_reading", nullable = false, precision = 14, scale = 3)
    private BigDecimal previousReading;

    @Column(name = "current_reading", nullable = false, precision = 14, scale = 3)
    private BigDecimal currentReading;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal consumption;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "reading_year", nullable = false)
    private int readingYear;

    @Column(name = "reading_month", nullable = false)
    private int readingMonth;

    /** Id of the OPERATOR user who captured the reading (nullable for audit). */
    @Column(name = "recorded_by")
    private Long recordedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
