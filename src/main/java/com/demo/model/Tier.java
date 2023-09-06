package com.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.model.DateRange.getKeyDatesList;

public class Tier {
    private final double price;
    private final boolean appliesOnWeekends;
    private List<DateRange> datesApplied;
    private List<Discount> discountsApplied;

    public Tier(double price, List<DateRange> datesApplied, List<Discount> discountsApplied, boolean appliesWeekends) {
        this.price = price;
        this.datesApplied = datesApplied;
        this.discountsApplied = discountsApplied;
        this.appliesOnWeekends = appliesWeekends;
    }

    public Tier(double price, DateRange datesApplied, Discount discountApplied, boolean appliesWeekends) {
        this.price = price;
        this.datesApplied = List.of(datesApplied);
        this.appliesOnWeekends = appliesWeekends;

        var discountsList = new ArrayList<Discount>(); // Needs this List initialisation to be mutable
        discountsList.add(discountApplied);
        this.discountsApplied = discountsList;
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

        limitToDatesToQueryRange(queriedRange);

        var keyDates = keyDateList();

        for (int i = 0, keyDatesSize = keyDates.size() - 1; i < keyDatesSize; i++) {
            var range = new DateRange(keyDates.get(i), keyDates.get(i + 1).minusDays(1));
            if (datesApplied.stream().anyMatch(range::isWithin)) {

                var discountsInRange = discountsApplied.stream().filter(discount -> discount.appliesToRange(range))
                        .map(Discount::getDiscountRate).toList();

                var numberOfDays = appliesOnWeekends ? range.getDays() : range.getWeekdays();

                totalPrice += calculateEffectiveDiscount(discountsInRange) * price * numberOfDays;
            }
        }

        return totalPrice;
    }

    private void limitToDatesToQueryRange(DateRange queriedRange) {
        // Limit the dates for the tiers and discounts to only withing the relevant range.
        // Probably a bit overengineer-y, but helps  time complexity if things get big.
        datesApplied = datesApplied.stream().filter(dateRange -> dateRange.hasOverlap(queriedRange))
                .map(range -> range.getLimitedRange(queriedRange)).toList();
        discountsApplied = discountsApplied.stream().filter(discount -> discount.hasOverlap(queriedRange))
                .map(discount -> discount.getRangeLimitedDiscount(queriedRange))
                .collect(Collectors.toList());
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
    private List<LocalDate> keyDateList() {
        var dates = getKeyDatesList(datesApplied);
        dates.addAll(discountsApplied.stream().flatMap(discount -> discount.keyDates().stream()).toList());
        return dates.stream().sorted().distinct().toList();
    }
}
