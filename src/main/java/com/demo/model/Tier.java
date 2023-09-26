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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

@Serdeable
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
    @NotNull
    private List<DateRange> datesApplied;

    @OneToMany(mappedBy = "tier")
    @NotNull
    private List<Discount> discountList;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    public Tier(double price, List<DateRange> datesApplied, List<Discount> discountList, boolean appliesOnWeekends) {
        this.price = price;
        this.datesApplied = datesApplied;
        this.discountList = discountList;
        this.appliesOnWeekends = appliesOnWeekends;
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

    @Override
    public int hashCode() {
        return Objects.hash(price, appliesOnWeekends, discountList.size(), Set.of(discountList),
                datesApplied.size(), Set.of(datesApplied));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tier otherTier)) {
            return false;
        }

        return price.equals(otherTier.price)
                && appliesOnWeekends.equals(otherTier.appliesOnWeekends)
                && isEqualCollection(datesApplied, datesApplied)
                && isEqualCollection(discountList, discountList);
    }
}
