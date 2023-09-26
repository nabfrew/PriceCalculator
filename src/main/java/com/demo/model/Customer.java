package com.demo.model;


import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

@Serdeable
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @UuidGenerator
    String id;

    @OneToMany(mappedBy = "customer")
    List<Tier> tiers;

    @Convert(converter = DateRangeListConverter.class)
    @NotNull
    @Column(name = "free_days", columnDefinition = "int8range")
    private List<DateRange> freeDays;

    public Customer(String id, List<Tier> tiers, List<DateRange> freeDays) {
        this.id = id;
        this.tiers = tiers;
        this.freeDays = freeDays;
    }

    public Customer(List<Tier> tiers, List<DateRange> freeDays) {
        this.tiers = tiers;
        this.freeDays = freeDays;
    }

    public Customer() {
        this.tiers = new ArrayList<>();
        this.freeDays = new ArrayList<>();
    }

    public void applyFreeDays() {
        tiers.forEach(tier -> tier.addFreeDays(freeDays));
    }

    public List<Tier> getTiers() {
        return tiers;
    }

    public List<DateRange> getFreeDays() {
        return freeDays;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tiers, freeDays.size(), Set.of(freeDays));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Customer otherCustomer)) {
            return false;
        }

        if (freeDays == null ^ otherCustomer.freeDays == null) {
            return false;
        }

        if (freeDays != null && !isEqualCollection(freeDays, otherCustomer.freeDays)) {
            return false;
        }

        return id.equals(otherCustomer.id)
                && tiers.equals(otherCustomer.tiers)
                && isEqualCollection(freeDays, otherCustomer.freeDays);
    }
}
