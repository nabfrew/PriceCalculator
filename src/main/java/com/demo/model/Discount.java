package com.demo.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static com.demo.model.DateRange.getKeyDatesList;

public class Discount {
    private final double discountRate; // It's easiest in the calculation to have a fraction to multiply by than % discount.
    private Collection<DateRange> datesApplied;

    public Discount(double discountRate, Collection<DateRange> datesApplied) {
        this.discountRate = discountRate;
        this.datesApplied = datesApplied;
    }

    public Discount(double discountRate, DateRange datesApplied) {
        this.discountRate = discountRate;
        this.datesApplied = List.of(datesApplied);
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

    Collection<LocalDate> keyDates() {
        return getKeyDatesList(datesApplied);
    }

    public boolean appliesToRange(DateRange range) {
        return datesApplied.stream().anyMatch(range::isWithin);
    }

    public boolean hasOverlap(DateRange otherRange) {
        return datesApplied.stream().anyMatch(range -> range.hasOverlap(otherRange));
    }
}
