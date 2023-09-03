package com.demo.calculator;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class CalculatorTest {

    private double TIER_A_PRICE = 0.2;
    private double TIER_B_PRICE = 0.24;
    private double TIER_C_PRICE = 0.4;

    /**
     * Test case as specified in task:
     *
     * Customer X started using Service A (0.2) and Service C (0.24) 2019-09-20.
     * Customer X also had a discount of 20% between 2019-09-22 and 2019-09-24 for Service C.
     * What is the total price for Customer X up until 2019-10-01?
     *
     *
     *
     */
    @Test
    void testCase1() {
        var discountedDates = new DateRange(LocalDate.of(2019,9,22), LocalDate.of(2019, 9, 24));
        var querieDateRange = new DateRange(LocalDate.of(2019, 9, 20), LocalDate.of(2019, 10, 01));
        var tierDates = new DateRange(LocalDate.of(2019, 9,20), null);
        var discountC = new Discount(0.8, List.of(discountedDates));

        var tierA = new Tier(TIER_A_PRICE, List.of(tierDates), new ArrayList<>(), false);
        var tierB = new Tier(TIER_C_PRICE, List.of(tierDates), List.of(discountC), true);

        Calculator.price(List.of(tierA, tierB), querieDateRange);



    }


}