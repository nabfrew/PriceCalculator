package com.demo.data;

import com.demo.model.Customer;
import com.demo.model.DateRange;
import com.demo.model.Discount;
import com.demo.model.Tier;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class PriceCalculatorRepositoryTest {

    @Inject
    CustomerRepository repository;

    @Test
    void testCustomerRoundTrip() {
        var discount = new Discount(0.5, 1,2);
        var tier = new Tier(1, new DateRange(1,4), discount, true);

        var dbCustomer = repository.save(List.of(tier), List.of(new DateRange(1,1)));
        var customer = new Customer(dbCustomer.getId(), List.of(tier), List.of(new DateRange(1, 1)));

        assertEquals(customer, dbCustomer);
    }
}