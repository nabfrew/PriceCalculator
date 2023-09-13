package com.demo.model;


import io.micronaut.data.annotation.GeneratedValue;

import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    String id;

    @OneToMany(mappedBy = "customer")
    List<Tier> tiers;

    public List<DateRange> getFreeDays() {
        return freeDays;
    }

    @Convert(converter = DateRangeListConverter.class)
    @Column(name = "free_days", columnDefinition = "int8range")
    private List<DateRange> freeDays;

    public Customer(List<Tier> tiers, List<DateRange> freeDays) {
        this.tiers = tiers;
        this.freeDays = freeDays;

    }

    public Customer() {
        this.tiers = new ArrayList<>();
        this.freeDays = new ArrayList<>();
    }

    public List<Tier> getTiers() {
        return tiers;
    }

    public void applyFreeDays() {
        tiers.forEach(tier -> tier.addFreeDays(freeDays));
    }

    public String getId() {
        return id;
    }
}
