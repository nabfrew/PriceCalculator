package com.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.List;

import static com.demo.model.DateRange.getKeyDatesList;

@Entity
@Table(name = "discount")
public class Discount {
    @Column(name = "discount_rate")
    private Double discountRate;

    @Convert(converter = DateRangeListConverter.class)
    @Column(name = "date_ranges", columnDefinition = "int8range")
    private List<DateRange> datesApplied;

    @ManyToOne
    @JoinColumn(name = "tier_id")
    private Tier tier;

    @Id
    private Long id;

    public Discount(double discountRate, Collection<DateRange> datesApplied) {
        this.discountRate = discountRate;
        this.datesApplied = datesApplied.stream().sorted(DateRange::sortByEndDescendingComparator).toList();
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
    }

    public Discount getRangeLimitedDiscount(DateRange queriedRange) {
        datesApplied = datesApplied.stream().filter(dateRange -> dateRange.hasOverlap(queriedRange))
                .map(range -> range.getLimitedRange(queriedRange))
                .toList();
        return this;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    Collection<Long> keyDates() {
        return getKeyDatesList(datesApplied);
    }

    public boolean appliesToRange(DateRange range) {
        for (DateRange dateRange : datesApplied) {
            if (dateRange.end() <= range.start()) {
                // We can do this because discount dates are sorted by end descending.
                break;
            }
            if (range.isWithin(dateRange)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOverlap(DateRange otherRange) {
        return datesApplied.stream().anyMatch(range -> range.hasOverlap(otherRange));
    }
}
