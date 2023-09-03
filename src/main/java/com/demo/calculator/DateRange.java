package com.demo.calculator;

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

    public long overlappingDays(DateRange otherRange) {
        if (hasOverlap(otherRange)) {
            return 0;
        }
        return overlapStart(otherRange).until(overlapStart(otherRange)).getDays();
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

    static Collection<LocalDate> getFlattenedDateList(Collection<DateRange> dateRangeList) {
        var list = new ArrayList<LocalDate>();
        dateRangeList.forEach(dateRange -> {
            list.add(dateRange.start);
            list.add(dateRange.end.plusDays(1)); // Add one because the effect applies to the end date.
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
        // Adjust the start and end dates so that they are guaranteed to be on weekdays.
        LocalDate adjustedStart;
        if (start.getDayOfWeek().getValue() == 6) {
            adjustedStart = start.plusDays(2);
        } else {
            if (start.getDayOfWeek().getValue() == 7) adjustedStart = start.plusDays(1);
            else adjustedStart = start;
        }
        LocalDate adjustedEnd;
        if (end.getDayOfWeek().getValue() == 6) {
            adjustedEnd = end.minusDays(1);
        } else {
            if (end.getDayOfWeek().getValue() == 7) adjustedEnd = end.minusDays(2);
            else adjustedEnd = end;
        }

        // In case they are on the same weekend
        if (adjustedEnd.isBefore(adjustedStart)) {
            return 0;
        }
        var days = DAYS.between(adjustedStart, adjustedEnd) + 1;
        var weeks = days / 7;

        // Because we know the start and end are adjusted, it's safe to calculate as 5 days per week + remainder.
        return weeks * 5 + days % 7;
    }
}
