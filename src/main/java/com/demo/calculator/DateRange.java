package com.demo.calculator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public record DateRange(LocalDate start, LocalDate end) {
    public DateRange {
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
            list.add(dateRange.start());
            list.add(dateRange.end());
        });
        return list;
    }

    public boolean isWithin(DateRange otherRange) {
        return (start.isEqual(otherRange.start) || start.isAfter(otherRange.start))
                && (end.isEqual(otherRange.end) || end.isBefore(otherRange.end));
    }

    public double getDays() {
        return start.until(end).getDays();
    }
}
