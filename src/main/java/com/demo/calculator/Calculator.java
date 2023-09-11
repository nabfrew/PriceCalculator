package com.demo.calculator;

import com.demo.model.DateRange;
import com.demo.model.Tier;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Calculator {

    // These can be amended depending on how overlapping ranges within the same tier/discount should be applied.
    private static final Calculation ADD_TIER_PRICE = Double::sum;
    private static final Calculation APPLY_DISCOUNT_PRICE = ((existing, value) -> existing * value);

    /**
     * Gets the price for all tiers, accounting for discounts and free days.
     */
    public static double price(List<Tier> tiers, DateRange dateRange)  {
        return tiers.stream().mapToDouble(tier -> calculateTierPrice(tier, dateRange)).sum();
    }

    static double calculateTierPrice(Tier tier, DateRange dateRange) {
        var timeline = tierToTimeline(tier, dateRange);
        var timelilneIterator = timeline.entrySet().iterator();
        var total = 0.0;
        var appliesOnWeekends = tier.getAppliesOnWeekends();

        Map.Entry<Long, Double> date2;
        var date1 = timelilneIterator.next();
        while (timelilneIterator.hasNext()) {
            date2 = timelilneIterator.next();
            var range = new DateRange(date1.getKey(), date2.getKey());
            var days = appliesOnWeekends ? range.getDays() : range.getWeekdays();
            total += days * date1.getValue();

            date1 = date2;
        }

        return total;
    }

    public static SortedMap<Long, Double> tierToTimeline(Tier tier, DateRange queriedRange) {
        // List of dates on which a change in price takes effect, and what the price is.
        var resultTimeline = new TreeMap<Long, Double>();
        resultTimeline.put(queriedRange.end() + 1, 0.0); // after the range end it should be zero.

        for (var dateRange : tier.getDatesApplied()) {
            applyDateRangeToTimeline(tier.getPrice(), queriedRange, dateRange, resultTimeline, ADD_TIER_PRICE);

        }

        for (var discount : tier.getDiscountList()) {
            for (var dateRange : discount.getDatesApplied()) {
                applyDateRangeToTimeline(discount.getDiscountRate(), queriedRange, dateRange, resultTimeline, APPLY_DISCOUNT_PRICE);
            }
        }

        return resultTimeline;
    }

    private static void applyDateRangeToTimeline(double value, DateRange queriedRange, DateRange dateRange, TreeMap<Long, Double> resultTimeline, Calculation operation) {
        // Limit to the query
        var start = max(queriedRange.start(), dateRange.start());
        var end = min(queriedRange.end(), dateRange.end());
        if (end < start) {
            return;
        }

        addToTimeline(resultTimeline, start, end, value, operation);
    }

    private static void addToTimeline(TreeMap<Long, Double> resultTimeline, long start, long end, Double value, Calculation calculation) {
        // Set the start date
        var existingPrice = 0.0;
        var existingPriceEntry = resultTimeline.floorEntry(start);
        if (existingPriceEntry != null) {
            existingPrice = existingPriceEntry.getValue();
        }
        var newPrice = calculation.newPrice(existingPrice, value);

        resultTimeline.put(start, newPrice);

        // Update all the dates within the range.
        for (var dateToUpdate : resultTimeline.subMap(start, false, end, false).entrySet()) {
            existingPrice = dateToUpdate.getValue();
            newPrice = calculation.newPrice(existingPrice, value);
            resultTimeline.put(dateToUpdate.getKey(), newPrice);
        }

        // *After* range ends, restore to whatever the previous price was.
        // The dates limited to query, and the value already placed at queriedRange + 1 prevents overwriting.
        resultTimeline.putIfAbsent(end + 1, existingPrice);
    }

    public interface Calculation {
        double newPrice(double existing, double value);
    }
}
