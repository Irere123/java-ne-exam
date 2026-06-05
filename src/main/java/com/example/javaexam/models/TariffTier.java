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
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One consumption band of a {@link Tariff}. Units consumed within
 * {@code [minUnits, maxUnits)} are charged at {@code ratePerUnit}. A
 * {@code null} {@code maxUnits} means the band is unbounded (top tier).
 */
@Entity
@Table(name = "tariff_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "min_units", nullable = false, precision = 14, scale = 3)
    private BigDecimal minUnits;

    /** Upper bound (exclusive); {@code null} for the unbounded top tier. */
    @Column(name = "max_units", precision = 14, scale = 3)
    private BigDecimal maxUnits;

    @Column(name = "rate_per_unit", nullable = false, precision = 12, scale = 4)
    private BigDecimal ratePerUnit;
}
