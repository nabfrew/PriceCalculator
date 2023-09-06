package com.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TierTest {

    @Test
    void testSingleDateRangeFullyWithinQuery() {
        var numberOfDays = 5;
        var pricePerDay = 10;
        var querieDateRange = createDateRange(100);

        var datesApplied = List.of(createDateRange(numberOfDays));
        var tier = new Tier(pricePerDay, datesApplied, new ArrayList<>(), true);

        assertEquals(numberOfDays * pricePerDay, tier.calculateTotalPrice(querieDateRange));
    }

    @Test
    void testSingleDateRangeAndDiscountFullyWithinQuery() {
        var numberOfDays = 5;
        var pricePerDay = 10;
        var discountedDays = 2;
        var discountRate = 0.5;
        var querieDateRange = createDateRange(100);

        var discount = new Discount(discountRate, List.of(createDateRange(discountedDays)));
        var datesApplied  = List.of(createDateRange(numberOfDays));

        var tier = new Tier(pricePerDay, datesApplied, List.of(discount), true);

        var expectedPrice = (numberOfDays - discountedDays) * pricePerDay + discountedDays * pricePerDay * discountRate;

        assertEquals(expectedPrice, tier.calculateTotalPrice(querieDateRange));
    }

    @Test
    void addFreeDays() {
        var date1 = LocalDate.of(1, 1, 1);
        var date2 = LocalDate.of(2, 1, 1);
        var date3 = LocalDate.of(3, 1, 1);

        var range1 = new DateRange(date1, date3);
        var range2 = new DateRange(date1, date2); // free days.

        // 50% discount on 2/day -> 1/day.
        var tier = new Tier(2, range1, new Discount(0.5, range1), true);

        tier.addFreeDays(List.of(range2));

        // Checking the full range, but price should only apply in range after free days.
        assertEquals(DAYS.between(date2, date3), tier.calculateTotalPrice(range1));
    }

    /**
     * Convenience methods for test.
     */
    private static DateRange createDateRange(int numberOfDays) {
        return createDateRange(LocalDate.of(1,1,1), numberOfDays);
    }

    private static DateRange createDateRange(LocalDate start, int numberOfDays) {
        return new DateRange(start, start.plusDays(numberOfDays - 1)); // -1 because date ranges are inclusive on both ends.
    }
}