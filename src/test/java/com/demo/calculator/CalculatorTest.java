package com.demo.calculator;

import com.demo.model.DateRange;
import com.demo.model.Discount;
import com.demo.model.Tier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.demo.calculator.Calculator.calculateTierPrice;
import static java.time.temporal.ChronoUnit.DAYS;
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
        var tierC = new Tier(TIER_C_PRICE, tierDates, discountC, true);

        // Check that the tiers are calculated correctly individually.
        assertEquals(1.6, calculateTierPrice(tierA, querieDateRange), 1e-4); // Arbitrary good-enough delta.
        assertEquals(4.56, calculateTierPrice(tierC, querieDateRange), 1e-4);

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
        assertEquals(expectedTierB, calculateTierPrice(tierB, querieDateRange), 1e-4, String.format("%s\t", expectedTierB - calculateTierPrice(tierB, querieDateRange) ));
        assertEquals(expectedTierC, calculateTierPrice(tierC, querieDateRange), 1e-4, String.format("%s\t", expectedTierC - calculateTierPrice(tierC, querieDateRange) ));

        // Check that final result is correct.
        var price = Calculator.price(List.of(tierB, tierC), querieDateRange);
        assertEquals(expectedTierB + expectedTierC, price, 1e-4, String.format("%s", 161.392 -price));
    }

    @Test
    void testSingleDateRangeFullyWithinQuery() {
        var numberOfDays = 5;
        var pricePerDay = 10;
        var querieDateRange = createDateRange(100);

        var datesApplied = List.of(createDateRange(numberOfDays));
        var tier = new Tier(pricePerDay, datesApplied, new ArrayList<>(), true);

        assertEquals(numberOfDays * pricePerDay, calculateTierPrice(tier, querieDateRange));
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

        assertEquals(expectedPrice, calculateTierPrice(tier, querieDateRange));
    }

    @Test
    void testAddFreeDays() {
        var date1 = LocalDate.of(1, 1, 1);
        var date2 = LocalDate.of(2, 1, 1);
        var date3 = LocalDate.of(3, 1, 1);

        var range1 = new DateRange(date1, date3);
        var range2 = new DateRange(date1, date2); // free days.

        // 50% discount on 2/day -> 1/day.
        var tier = new Tier(2, range1, new Discount(0.5, range1), true);

        tier.addFreeDays(List.of(range2));

        // Checking the full range, but price should only apply in range after free days.
        assertEquals(DAYS.between(date2, date3), calculateTierPrice(tier, range1));
    }

    /**
     * Check there's no hidden bad time complexity that could come into play
     * in unlikely worst-case scenario over long time periods with rapidly adapting pricing...
     */
    @Test
    @Timeout(1) // 1 second, quite generous.
    void testPerformance() {
        testPerformance(false);
        testPerformance(true);
    }

    private void testPerformance(boolean appliesOnWeekends) {
        var nrOfDateRangesPerTier = 500;
        var nrOfDiscountsPerTier = 500;
        var nrOfRangesPerDiscount = 500;

        var tier = generateTierWithLotsOfDiscounts(nrOfDiscountsPerTier, nrOfRangesPerDiscount, nrOfDateRangesPerTier, appliesOnWeekends);

        var queryStartDate = LocalDate.of(1, 1, 1).plusDays(200);
        var queryDateRange = new DateRange(queryStartDate, null);

        // For a real production system I'd look into some better, automated, way of tracking the performance over time.
        var time = System.currentTimeMillis();
        var calculatedPrice = calculateTierPrice(tier, queryDateRange);
        System.out.println("Time " +  (appliesOnWeekends ? "with" : "without") +  " weekends = " + ((double)(System.currentTimeMillis() - time))/1000);
        System.out.println("price " +  (appliesOnWeekends ? "with" : "without") +  " weekends = " + (calculatedPrice));
    }

    private Tier generateTierWithLotsOfDiscounts(int nrOfDiscountsPerTier, int nrOfRangesPerDiscount, int nrOfRangesPerTier, boolean appliesOnWeekends) {
        var discounts = new ArrayList<Discount>();

        for (var j = 1; j <= 1 + nrOfDiscountsPerTier; j++) {
            discounts.add(generateDiscount(j, nrOfRangesPerDiscount));
        }
        return new Tier(1, generateDateRanges(1, nrOfRangesPerTier, 1000), discounts, appliesOnWeekends);
    }

    private Discount generateDiscount(int j, int nrOfRanges) {
        var dateRanges = generateDateRanges(j, nrOfRanges, 1);
        return new Discount(0.99, dateRanges);
    }

    private static ArrayList<DateRange> generateDateRanges(int j, int nrOfRanges, int years) {
        var dateRanges = new ArrayList<DateRange>();

        for (var k = j; k < j + nrOfRanges; k++) {
            dateRanges.add(new DateRange(LocalDate.of(1, 1, 1).plusDays(k), LocalDate.of(years, 1, 1).plusDays(k)));
        }
        return dateRanges;
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