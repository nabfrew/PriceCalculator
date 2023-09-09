package com.demo.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;

public final class DateRange {
    private final long start;
    private final long end;
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

    public long getDays() {
        return end - start;
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
