package com.demo.calculator;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TierTest {

    @Test
    void testSingleDateRangeFullyWithinQuery() {
        var numberOfDays = 5;
        var pricePerDay = 10;
        var querieDateRange = createDateRange(100);

        var datesApplied = List.of(createDateRange(numberOfDays));
        var tier = new Tier(pricePerDay, datesApplied, new ArrayList<>());

        assertEquals(numberOfDays * pricePerDay, tier.calculateTotalPrice(querieDateRange));
    }

    @Test
    void testSingelDateRangeAndDiscountFullyWithinQuery() {
        var numberOfDays = 5;
        var pricePerDay = 10;
        var discountedDays = 2;
        var discountRate = 0.5;
        var querieDateRange = createDateRange(100);

        var discount = new Discount(discountRate, List.of(createDateRange(discountedDays)));

        var datesApplied  = List.of(createDateRange(numberOfDays));

        var tier = new Tier(pricePerDay, datesApplied, List.of(discount));

        var expectedPrice = (numberOfDays - discountedDays) * pricePerDay + discountedDays * pricePerDay * discountRate;

        assertEquals(expectedPrice, tier.calculateTotalPrice(querieDateRange));
    }

    /**
     * Convenience methods for test.
     */
    private static DateRange createDateRange(int numberOfDays) {
        return createDateRange(LocalDate.of(1,1,1), numberOfDays);
    }

    private static DateRange createDateRange(LocalDate start, int numberOfDays) {
        return new DateRange(start, start.plusDays(numberOfDays));
    }
}