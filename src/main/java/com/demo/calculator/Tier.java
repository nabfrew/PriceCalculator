package com.demo.calculator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.calculator.DateRange.getFlattenedDateList;

public class Tier {
    private final float price;
    private List<DateRange> datesApplied;
    private List<Discount> discountsApplied;

    public Tier(float price, List<DateRange> datesApplied, List<Discount> discountsApplied) {
        this.price = price;
        this.datesApplied = datesApplied;
        this.discountsApplied = discountsApplied;
    }

    /**
     * For the purposes of the calculation, the free days can just be considered 100% discounts.
     */
    public void addFreeDays(Collection<DateRange> freeDays) {
        discountsApplied.add(new Discount(0, freeDays));
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
    public double calculateTotalPrice(DateRange queriedRange) {
        var totalPrice = 0.0;

        // Limit the dates for the tiers and discounts to only withing the relevant range.
        datesApplied = datesApplied.stream().filter(dateRange -> dateRange.hasOverlap(queriedRange))
                .map(range -> range.getLimitedRange(queriedRange)).toList();
        discountsApplied = discountsApplied.stream().filter(discount -> discount.hasOverlap(queriedRange))
                .map(discount -> discount.getRangeLimitedDiscount(queriedRange))
                .collect(Collectors.toList());

        var keyDates = flattenedDateList();

        for (int i = 0, keyDatesSize = keyDates.size() - 1; i < keyDatesSize; i++) {
            var range = new DateRange(keyDates.get(i), keyDates.get(i + 1));
            if (datesApplied.stream().anyMatch(range::isWithin)) {

                // Assuming that in case of multiple discounts applied, they
                var discountsInRange = discountsApplied.stream().filter(discount -> discount.appliesToRange(range))
                        .map(Discount::getDiscountRate).toList();


                totalPrice += calculateEffectiveDiscount(discountsInRange) * price * range.getDays();
            }
        }

        return totalPrice;
    }

    /**
     * Not sure how multiple discounts should be applied - assuming they just multiply together here.
     */
    private double calculateEffectiveDiscount(List<Double> discountsInRange) {
        return discountsInRange.stream().mapToDouble(Double::doubleValue).reduce(1, (a, b) -> a * b);
    }

    /**
     * Gets a chronological list of all cases where there is a change in the effective price.
     */
    private List<LocalDate> flattenedDateList() {
        var dates = getFlattenedDateList(datesApplied);
        dates.addAll(discountsApplied.stream().flatMap(discount -> discount.flattenedDates().stream()).toList());
        return dates.stream().sorted().toList();
    }
}
