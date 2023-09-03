package com.demo.calculator;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    private final double TIER_A_PRICE = 0.2;
    private final double TIER_B_PRICE = 0.24;
    private final double TIER_C_PRICE = 0.4;

    /**
     * Test case as specified in task:
     * <p>
     * Customer X started using Service A (0.2) and Service C (0.24) 2019-09-20.
     * Customer X also had a discount of 20% between 2019-09-22 and 2019-09-24 for Service C.
     * What is the total price for Customer X up until 2019-10-01?
     *                  20 21 22 23 24 25 26 27 28 29 30 1
     *                  F  S  S  M  T  W  Th F  S  S  M  T
     * Service A        o  x  x  o  o  o  o  o  x  x  o  o  = 8 days * 0.2 = 1.6
     * Service C        o  o  d  d  d  o  o  o  o  o  o  o  = 9 days * 0.4 + 3 days * 0.4 * 0.8 = 4.56
     *                                                      = 6.16
     */
    @Test
    void testCase1() {
        var discountedDates = new DateRange(LocalDate.of(2019,9,22), LocalDate.of(2019, 9, 24));
        var querieDateRange = new DateRange(LocalDate.of(2019, 9, 20), LocalDate.of(2019, 10, 1));
        var tierDates = new DateRange(LocalDate.of(2019, 9,20), null);
        var discountC = new Discount(0.8, List.of(discountedDates));

        var tierA = new Tier(TIER_A_PRICE, List.of(tierDates), new ArrayList<>(), false);
        var tierC = new Tier(TIER_C_PRICE, List.of(tierDates), List.of(discountC), true);

        // Check that the tiers are calculated correctly individually.
        assertEquals(1.6, tierA.calculateTotalPrice(querieDateRange), 1e-4);
        assertEquals(4.56, tierC.calculateTotalPrice(querieDateRange), 1e-4);

        // Check that final result is correct.
        var price = Calculator.price(List.of(tierA, tierC), querieDateRange);
        assertEquals(6.16, price, 1e-4);
    }

    /**
     * Test case as specified in task:
     * <p>
     * Customer Y started using Service B and Service C 2018-01-01. Customer Y
     * had 200 free days and a discount of 30% for the rest of the time. What
     * is the total price for Customer Y up until 2019-10-01?
     * <p>
     * According to Wolfram Alpha:
     * 143 weekdays from start + 199 days (not counting 1st twice)
     * 30% discount runs from 2018-07-20 onwards. 313 weekdays, 439 days
     * Tier B : 0.24 * 313 *0.7 = 52.584
     * Tier C : 0.4 * 439 *0.7 = 122.92
     *
     */
    @Test
    void testCase2() {
        var startDate = LocalDate.of(2018, 1, 1);
        var querieDateRange = new DateRange(startDate, LocalDate.of(2019, 10, 1));

        var freePeriod = 200;
        var discountRate = 0.7;
        var discountDays = 439;
        var discountWeekdays = 313;
        var expectedTierB = discountRate * discountWeekdays * TIER_B_PRICE;
        var expectedTierC = discountRate * discountDays * TIER_C_PRICE;


        var discountFree = new Discount(0, List.of(new DateRange(startDate, startDate.plusDays(freePeriod - 1)))); // -1 because first day is included.
        var discount30 = new Discount(0.7, List.of(new DateRange(startDate.plusDays(freePeriod), null)));
        var discountList = List.of(discountFree, discount30);

        var tierB = new Tier(TIER_B_PRICE, List.of(new DateRange(startDate, null)), discountList, false);
        var tierC = new Tier(TIER_C_PRICE, List.of(new DateRange(startDate, null)), discountList, true);

        // Check that the tiers are calculated correctly individually.
        assertEquals(expectedTierB, tierB.calculateTotalPrice(querieDateRange), 1e-4, String.format("%s\t", expectedTierB - tierB.calculateTotalPrice(querieDateRange) ));
        assertEquals(expectedTierC, tierC.calculateTotalPrice(querieDateRange), 1e-4, String.format("%s\t", expectedTierC - tierC.calculateTotalPrice(querieDateRange) ));

        // Check that final result is correct.
        var price = Calculator.price(List.of(tierB, tierC), querieDateRange);
        assertEquals(expectedTierB + expectedTierC, price, 1e-4, String.format("%s", 161.392 -price));
    }
}