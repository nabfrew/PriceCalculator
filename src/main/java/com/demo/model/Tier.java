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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.model.DateRange.getKeyDatesList;

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
        this.datesApplied = datesApplied.stream().sorted().toList();
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
    public void addFreeDays(Collection<DateRange> freeDays) {
        discountList.add(new Discount(0, freeDays));
    }

    public void limitToDatesToQueryRange(DateRange queriedRange) {
        // Limit the dates for the tiers and discounts to only withing the relevant range.
        // Probably a bit overengineer-y, but helps  time complexity if things get big.
        datesApplied = datesApplied.stream().filter(dateRange -> dateRange.hasOverlap(queriedRange))
                .map(range -> range.getLimitedRange(queriedRange)).toList();
        discountList = discountList.stream().filter(discount -> discount.hasOverlap(queriedRange))
                .map(discount -> discount.getRangeLimitedDiscount(queriedRange))
                .collect(Collectors.toList());
    }

    /**
     * Gets a chronological list of all cases where there is a change in the effective price.
     */
    public List<Long> keyDateList() {
        var dates = getKeyDatesList(datesApplied);
        dates.addAll(discountList.stream().flatMap(discount -> discount.keyDates().stream()).toList());
        return dates.stream().sorted().distinct().toList();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<DateRange> getDatesApplied() {
        return datesApplied;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getAppliesOnWeekends() {
        return appliesOnWeekends;
    }

    public List<Discount> getDiscountList() {
        return discountList;
    }
}
