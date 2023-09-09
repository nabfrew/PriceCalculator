package com.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;

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
    }

    public List<DateRange> getDatesApplied() {
        return datesApplied;
    }

    public double getDiscountRate() {
        return discountRate;
    }

}
