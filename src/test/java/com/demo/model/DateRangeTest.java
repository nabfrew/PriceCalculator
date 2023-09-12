package com.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeTest {

    @Test
    void testGetWeekdays_SameWeekday() {
        LocalDate startDate = LocalDate.of(1, 1, 1); // Monday
        LocalDate endDate = LocalDate.of(1, 1, 1);
        assertEquals(0, new DateRange(startDate, endDate).getWeekdays());
    }

    @Test
    void testGetWeekdays_FridayToTuesdayAfterNext() {
        LocalDate startDate = LocalDate.of(1, 1, 5); // Friday
        LocalDate endDate = LocalDate.of(1, 1, 16); // Tuesday
        assertEquals(7, new DateRange(startDate, endDate).getWeekdays());
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
        assertEquals(4, new DateRange(startDate, endDate).getWeekdays());
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
        assertEquals(14, new DateRange(startDate, endDate).getWeekdays());
    }
}