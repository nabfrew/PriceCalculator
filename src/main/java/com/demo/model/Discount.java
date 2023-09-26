package com.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.GenerationType.AUTO;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

@Serdeable
@Entity
@Table(name = "discount")
public class Discount {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(name = "discount_rate")
    private Double discountRate;

    @Convert(converter = DateRangeListConverter.class)
    @Column(name = "date_ranges", columnDefinition = "int8range")
    @NotNull
    private List<DateRange> datesApplied;

    @ManyToOne
    @JoinColumn(name = "tier_id")
    @JsonIgnore
    private Tier tier;


    public Discount(double discountRate, List<DateRange> datesApplied) {
        this.discountRate = discountRate;
        this.datesApplied = datesApplied;
    }

    public Discount(double discountRate, DateRange datesApplied) {
        this.discountRate = discountRate;
        this.datesApplied = List.of(datesApplied);
    }

    // convenience for testing

    public Discount(double discountRate, long start, long end) {
        this.discountRate = discountRate;
        this.datesApplied = List.of(new DateRange(start, end));
    }
    public Discount() {
        discountRate = 1.0;
        datesApplied = List.of();
    }

    public List<DateRange> getDatesApplied() {
        return datesApplied;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Discount otherDiscount)) {
            return false;
        }
        return id.equals(otherDiscount.id)
                && discountRate.equals(otherDiscount.discountRate)
                 && isEqualCollection(datesApplied, otherDiscount.datesApplied);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, discountRate, datesApplied.size(), new HashSet<>(datesApplied));
    }
}
