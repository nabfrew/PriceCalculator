package com.demo.data;

import com.demo.model.Customer;
import com.demo.model.DateRange;
import com.demo.model.Tier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(String customerId);

    void save(Customer customer);

    Customer save(@NotNull @NotBlank List<Tier> tiers, List<DateRange> freeDays);
}