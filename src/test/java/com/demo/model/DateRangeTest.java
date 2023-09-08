package com.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.demo.model.DateRange.getKeyDatesList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeTest {

    @Test
    void testGetWeekdays_SameWeekday() {
        LocalDate startDate = LocalDate.of(1, 1, 1); // Monday
        LocalDate endDate = LocalDate.of(1, 1, 1);
        assertEquals(1, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_FridayToTuesdayAfterNext() {
        LocalDate startDate = LocalDate.of(1, 1, 5); // Monday
        LocalDate endDate = LocalDate.of(1, 1, 16);
        assertEquals(8, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_SameWeekend() {
        LocalDate startDate = LocalDate.of(1, 1, 6); // Saturday
        LocalDate endDate = LocalDate.of(1, 1, 6);
        assertEquals(0, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_DifferentWeekdays() {
        LocalDate startDate = LocalDate.of(1, 1, 1); // Monday
        LocalDate endDate = LocalDate.of(1, 1, 5);   // Friday
        assertEquals(5, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_MultipleWeeks() {
        LocalDate startDate = LocalDate.of(1, 1, 1); // Monday
        LocalDate endDate = LocalDate.of(1, 1, 14);  // Sunday
        assertEquals(10, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_WeekWithWeekend() {
        LocalDate startDate = LocalDate.of(1, 1, 6); // Saturday
        LocalDate endDate = LocalDate.of(1, 1, 13);  // Saturday
        assertEquals(5, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_SaturdayToSundaySameWeek() {
        LocalDate startDate = LocalDate.of(1, 1, 6); // Saturday
        LocalDate endDate = LocalDate.of(1, 1, 7);  // Saturday
        assertEquals(0, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_SaturdayToSundayPlusOneWeek() {
        LocalDate startDate = LocalDate.of(1, 1, 6); // Saturday
        LocalDate endDate = LocalDate.of(1, 1, 14);  // Saturday
        assertEquals(5, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_MultipleWeeksWithoutWeekends() {
        LocalDate startDate = LocalDate.of(1, 1, 3);  // Wednesday
        LocalDate endDate = LocalDate.of(1, 1, 23);   // Tuesday
        assertEquals(15, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetDatesDateList_sameDate() {
        var date1 = LocalDate.of(1,1,1);

        var flattenedList = getKeyDatesList(List.of(new DateRange(date1, date1)));

        // Change applied after the single-day range.
        assertEquals(List.of(date1.toEpochDay(), date1.plusDays(1).toEpochDay()), flattenedList);
    }

    @Test
    void testGetKeyDateList() {
        var date1 = LocalDate.of(1,1,1);
        var date2 = date1.plusDays(1);
        var date3 = date2.plusDays(1);

        var range1 = new DateRange(date1, date2);
        var range2 = new DateRange(date3, date3);

        // date3 should be duplicated by range1 end & range2 start
        var flattenedList = getKeyDatesList(List.of(range1, range2));

        // Duplicates get removed later.
        assertEquals(List.of(date1.toEpochDay(), date3.toEpochDay(), date3.toEpochDay(), date3.plusDays(1).toEpochDay()), flattenedList);
    }
}