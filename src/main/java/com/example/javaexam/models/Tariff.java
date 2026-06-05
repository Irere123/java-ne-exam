package com.example.javaexam.models;

import com.example.javaexam.models.enums.MeterType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * A versioned tariff for a meter type (Task 4). Bundles the consumption rates
 * (as tiers), a fixed service charge, a VAT rate, and a late-payment penalty
 * rate. A flat tariff is just a single tier spanning {@code [0, +inf)}.
 *
 * <p>Versioning + {@code effectiveFrom} implement the rule that new tariffs
 * only apply to future billing cycles: billing always selects the latest
 * version whose {@code effectiveFrom} is on or before the billing period.
 */
@Entity
@Table(
        name = "tariffs",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_tariffs_type_version",
                columnNames = {"meter_type", "version"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 20)
    private MeterType meterType;

    @Column(nullable = false)
    private int version;

    @Column(name = "service_charge", nullable = false, precision = 14, scale = 2)
    private BigDecimal serviceCharge;

    /** VAT percentage, e.g. {@code 18.00} for 18%. */
    @Column(name = "vat_rate", nullable = false, precision = 6, scale = 2)
    private BigDecimal vatRate;

    /** Late-payment penalty percentage applied to an overdue balance. */
    @Column(name = "penalty_rate", nullable = false, precision = 6, scale = 2)
    private BigDecimal penaltyRate;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TariffTier> tiers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void addTier(TariffTier tier) {
        tier.setTariff(this);
        this.tiers.add(tier);
    }
}
