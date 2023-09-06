package com.demo.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static java.time.temporal.ChronoUnit.DAYS;

public record DateRange(LocalDate start, LocalDate end) {
    public DateRange {
        // If null assume ongoing.
        if (end == null) {
            end = LocalDate.MAX;
        }
        if (start.isAfter(end)) {
            throw new DateTimeException("start date cannot be after end date");
        }
    }

    boolean hasOverlap(DateRange otherRange) {
        return !otherRange.start.isAfter(end) && !otherRange.end.isBefore(start);
    }

    private LocalDate overlapStart(DateRange otherRange) {
        return start.isAfter(otherRange.start) ? start : otherRange.start;
    }

    private LocalDate overlapEnd(DateRange otherRange) {
        return end.isBefore(otherRange.end) ? end : otherRange.end;
    }

    /**
     * Limits the range
     */
    public DateRange getLimitedRange(DateRange limitingRange) {
        return new DateRange(overlapStart(limitingRange), overlapEnd(limitingRange));
    }

    /**
     * Extracts all the dates where a change occurs in applied prices. As all date ranges are inclusive the start and
     * end, dates of changes are ON the start day, and the day AFTER the end date, so +1 is added to all end dates.
     */
    static Collection<LocalDate> getKeyDatesList(Collection<DateRange> dateRangeList) {
        var list = new ArrayList<LocalDate>();
        dateRangeList.forEach(dateRange -> {
            list.add(dateRange.start);
            list.add(dateRange.end.plusDays(1));
        });
        return list;
    }

    public boolean isWithin(DateRange otherRange) {
        return (start.isEqual(otherRange.start) || start.isAfter(otherRange.start))
                && (end.isEqual(otherRange.end) || end.isBefore(otherRange.end));
    }

    public long getDays() {
        return DAYS.between(start, end) + 1; // Add 1 to include both start and end dates
    }

    /**
     * Calculates the number of weekdays (Mondays to Fridays) between the two specified dates.
     * There is probably a much smarter way of doing this...
     */
    public long getWeekdays() {

        var days = getDays();
        var fullWeeks = days / 7; // Truncates because days is long.

        // Work out how many of the days that don't fit into a full week are weekdays.
        var strayDays = days % 7;
        var weekdayStrays = 0;
        var dayOfWeek = start.getDayOfWeek().getValue();
        for (int i = 1 ; i <= strayDays; i++) {
            if (dayOfWeek != 6 && dayOfWeek != 7) {
                weekdayStrays++;
            }
            // modular arithmetic magic to ensure it loops back to Monday (1) after Sunday (7)
            dayOfWeek = (dayOfWeek % 7) + 1;
        }

        return fullWeeks * 5 + weekdayStrays;
    }
}
