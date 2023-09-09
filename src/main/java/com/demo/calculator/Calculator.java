package com.demo.calculator;

import com.demo.model.DateRange;
import com.demo.model.Discount;
import com.demo.model.Tier;

import java.util.List;

public class Calculator {
    /**
     * Gets the price for all tiers, accounting for discounts and free days.
     */
    public static double price(List<Tier> tiers, DateRange dateRange)  {
        return tiers.stream().mapToDouble(tier -> calculateTierPrice(tier, dateRange)).sum();
    }

    /**
     * Gets the total cost for the tier, accounting for discounts.
     * It's not specified in the task if multiple discounts can be applied.
     * Here, I am assuming they can - this complicates the calculation by a
     * lot, but keeps things flexible and allows collapsing 'free day' to a
     * 100% discount, without dedicated logic.
     * Time complexity could probably be improved here, but you'd need very
     * long, frequently updating histories for it to be worthwhile.
     */
    public static double calculateTierPrice(Tier tier, DateRange queriedRange) {
        var totalPrice = 0.0;

        tier.limitToDatesToQueryRange(queriedRange);

        var keyDates = tier.keyDateList();

        for (int i = 0, keyDatesSize = keyDates.size() - 1; i < keyDatesSize; i++) {
            var range = new DateRange(keyDates.get(i), keyDates.get(i + 1)- 1);
            if (tier.getDatesApplied().stream().anyMatch(range::isWithin)) {

                var discountsInRange = tier.getDiscountList().stream().filter(discount -> discount.appliesToRange(range))
                        .map(Discount::getDiscountRate).toList();

                var numberOfDays = Boolean.TRUE.equals(tier.getAppliesOnWeekends()) ? range.getDays() : range.getWeekdays();

                totalPrice += calculateEffectiveDiscount(discountsInRange) * tier.getPrice() * numberOfDays;
            }
        }

        return totalPrice;
    }

    /**
     * Not sure how multiple discounts should be applied - assuming they just multiply together here.
     */
    private static double calculateEffectiveDiscount(List<Double> discountsInRange) {
        return discountsInRange.stream().mapToDouble(Double::doubleValue).reduce(1, (a, b) -> a * b);
    }
}
