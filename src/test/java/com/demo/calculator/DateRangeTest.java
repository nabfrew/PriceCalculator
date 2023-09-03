package com.demo.calculator;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeTest {

    @Test
    void testGetWeekdays_SameWeekday() {
        LocalDate startDate = LocalDate.of(1, 1, 1); // Monday
        LocalDate endDate = LocalDate.of(1, 1, 1);
        assertEquals(1, new DateRange(startDate, endDate).getWeekdays());
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
        LocalDate endDate = LocalDate.of(1, 1, 23);   // Tuesday +
        assertEquals(15, new DateRange(startDate, endDate).getWeekdays());
    }

}