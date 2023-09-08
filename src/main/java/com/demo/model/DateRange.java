package com.demo.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class DateRange implements Comparable<DateRange> {
    private final long start;
    private final long end;
    private static final long MAX = LocalDate.MAX.toEpochDay();
    private final int startWeekday; // (monday = 1, sunday = 7)

    public DateRange(long start, long end) {
        this.start = start;
        this.end = end;
        this.startWeekday = LocalDate.ofEpochDay(start).getDayOfWeek().getValue();
    }

    public DateRange(LocalDate start, LocalDate end) {
        // If null assume ongoing.
        if (end == null) {
            end = LocalDate.MAX;
        }
        if (start.isAfter(end)) {
            throw new DateTimeException("start date cannot be after end date");
        }
        this.start = start.toEpochDay();
        this.end = end.toEpochDay();
        this.startWeekday = start.getDayOfWeek().getValue();
    }

    boolean hasOverlap(DateRange otherRange) {
        return otherRange.start <= end && otherRange.end >= start;
    }

    private long overlapStart(DateRange otherRange) {
        return max(start, otherRange.start);
    }

    private long overlapEnd(DateRange otherRange) {
        return min(end, otherRange.end);
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
    static Collection<Long> getKeyDatesList(Collection<DateRange> dateRangeList) {
        var list = new ArrayList<Long>();
        dateRangeList.forEach(dateRange -> {
            var add = dateRange.end + 1 == MAX ? 0 : 1;
            list.add(dateRange.start);
            list.add(dateRange.end + add);
        });
        return list;
    }

    public boolean isWithin(DateRange otherRange) {
        return (start >= otherRange.start)
                && (end <= otherRange.end);
    }

    public long getDays() {
        return end - start + 1; // Add 1 to include both start and end dates
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
        var dayOfWeek = startWeekday;
        for (int i = 1; i <= strayDays; i++) {
            if (dayOfWeek != 6 && dayOfWeek != 7) {
                weekdayStrays++;
            }
            // modular arithmetic magic to ensure it loops back to Monday (1) after Sunday (7)
            dayOfWeek = (dayOfWeek % 7) + 1;
        }

        return fullWeeks * 5 + weekdayStrays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(start, dateRange.start) && Objects.equals(end, dateRange.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    /**
     * Comparator by end descending, then by start descending.
     */
    public int sortByEndDescendingComparator(DateRange other) {
        if (end != other.end) {
            return Long.compare(other.end, end);
        }
        return Long.compare(other.start, start);
    }

    /**
     * default comparator first by start ascending, then end ascending
     *
     * @param other the object to be compared.
     */
    @Override
    public int compareTo(DateRange other
    ) {
        if (start != other.start) {
            return Long.compare(start, other.start);
        }
        return Long.compare(end, other.end);
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    @Override
    public String toString() {
        return "DateRange[" +
                "start=" + start + ", " +
                "end=" + end + ']';
    }

}
