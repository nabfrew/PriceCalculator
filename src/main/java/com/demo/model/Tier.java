package com.demo.model;

import io.micronaut.data.annotation.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tier")
public class Tier {

    @Id
    @GeneratedValue
    @Column(name = "tier_id")
    private Long id;

    @Column(name = "price")
    private final  Double price;

    @Column(name = "applies_on_weekends")
    private Boolean appliesOnWeekends;

    @Convert(converter = DateRangeListConverter.class)
    @Column(name = "date_ranges", columnDefinition = "int8range")
    private List<DateRange> datesApplied;

    @OneToMany(mappedBy = "tier")
    private List<Discount> discountList;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Tier(double price, List<DateRange> datesApplied, List<Discount> discountsApplied, boolean appliesWeekends) {
        this.price = price;
        this.datesApplied = datesApplied;
        this.discountList = discountsApplied;
        this.appliesOnWeekends = appliesWeekends;
    }

    public Tier(double price, DateRange datesApplied, Discount discountApplied, boolean appliesWeekends) {
        this.price = price;
        this.datesApplied = List.of(datesApplied);
        this.appliesOnWeekends = appliesWeekends;

        var discountsList = new ArrayList<Discount>(); // Needs this List initialisation to be mutable
        discountsList.add(discountApplied);
        this.discountList = discountsList;
    }

    public Tier() {
        this.price = 0.0;
    }

    /**
     * For the purposes of the calculation, the free days can just be considered 100% discounts.
     */
    public void addFreeDays(List<DateRange> freeDays) {
        discountList.add(new Discount(0, freeDays));
    }

    public List<DateRange> getDatesApplied() {
        return datesApplied;
    }

    public Double getPrice() {
        return price;
    }

    public boolean getAppliesOnWeekends() {
        return appliesOnWeekends;
    }

    public List<Discount> getDiscountList() {
        return discountList;
    }
}
