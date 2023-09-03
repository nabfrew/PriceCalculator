package com.demo.calculator;

import java.util.List;

public class Calculator {
    /**
     * Gets the price for all tiers, accounting for discounts and free days.
     */
    public static double price(List<Tier> tiers, DateRange dateRange)  {
        return tiers.stream().mapToDouble(tier -> tier.calculateTotalPrice(dateRange)).sum();
    }
}
