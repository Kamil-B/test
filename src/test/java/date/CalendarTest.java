package date;

import common.date.Calendar;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CalendarTest {

    private Calendar calendar;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<LocalDate> expectedMeetingDays;

    @Before
    public void setUp() {
        startDate = LocalDate.of(2018, Month.MARCH, 1);
        endDate = startDate.plus(30, ChronoUnit.DAYS);
    }

    @Test
    public void when_getFirstMeeting_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY);
        assertEquals(startDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)), calendar.iterator().next());
    }

    @Test
    public void when_getFirstMeetingWhenInSameDayAsStartDate_thenReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.THURSDAY);
        assertEquals(startDate, calendar.iterator().next());
    }

    @Test
    public void when_getAllMeetingsForOneMeetingInWeek_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY);
        expectedMeetingDays = new ArrayList<>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 2));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 9));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 16));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 23));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 30));

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(expectedMeetingDays, is(actualMeetingsDays));
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeek_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY, DayOfWeek.MONDAY);
        expectedMeetingDays = new ArrayList<>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 2));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 5));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 9));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 12));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 16));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 19));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 23));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 26));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 30));

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(expectedMeetingDays, is(actualMeetingsDays));
    }

    @Test
    public void when_twoIterators_then_ReturnSameValues() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY);
        Iterator<LocalDate> firstItr = calendar.iterator();
        Iterator<LocalDate> secondItr = calendar.iterator();
        assertEquals(firstItr.next(), secondItr.next());
    }

    @Test(expected = NoSuchElementException.class)
    public void when_noMeetingsBetweenStartAndEndDate_then_ReturnNoSuchElementException() {
        startDate = LocalDate.of(2018, Month.MARCH, 6);
        endDate = LocalDate.of(2018, Month.MARCH, 9);
        calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY);
        calendar.iterator().next();
    }
}