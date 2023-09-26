package com.demo.data;

import com.demo.model.Customer;
import com.demo.model.DateRange;
import com.demo.model.Tier;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final List<String> VALID_PROPERTY_NAMES = Arrays.asList("id");
    private final EntityManager entityManager;

    public CustomerRepositoryImpl (EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @ReadOnly
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(entityManager.find(Customer.class, customerId));
    }

    @Override
    @Transactional
    public void save(@NotNull Customer customer) {
        entityManager.persist(customer);
    }

    @Override
    @Transactional
    public Customer save(List<Tier> tiers, List<DateRange> freeDays) {
        Customer customer = new Customer(tiers, freeDays);
        entityManager.persist(customer);
        return customer;
    }
}
