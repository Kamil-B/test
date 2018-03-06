package date;

import common.date.Calendar;
import lombok.val;
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
    private List<LocalDate> expectedMeetingsMondaysAndFridays;

    @Before
    public void setUp() {
        startDate = LocalDate.of(2018, Month.MARCH, 1);
        endDate = startDate.plus(30, ChronoUnit.DAYS);

        expectedMeetingsMondaysAndFridays = new ArrayList<>();
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 2));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 5));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 9));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 12));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 16));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 19));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 23));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 26));
        expectedMeetingsMondaysAndFridays.add(LocalDate.of(2018, Month.MARCH, 30));
    }

    @Test
    public void when_getFirstMeeting_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY);
        assertEquals(startDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)), calendar.iterator().next());
    }

    @Test
    public void when_getFirstMeetingWhenInSameDayAsStartDate_thenReturnTrue() {
        val calendar = new Calendar(startDate, endDate, DayOfWeek.THURSDAY);
        assertEquals(startDate, calendar.iterator().next());
    }

    @Test
    public void when_getAllMeetingsForOneMeetingInWeekStartAtSameDay_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.THURSDAY);
        expectedMeetingDays = new ArrayList<>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 1));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 8));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 15));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 22));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 29));

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays, is(expectedMeetingDays));
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
        assertThat(actualMeetingsDays, is(expectedMeetingDays));
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeek_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY, DayOfWeek.MONDAY);
        expectedMeetingDays = new ArrayList<>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays, is(expectedMeetingsMondaysAndFridays));
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeekWhenDuplications_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.FRIDAY,
                DayOfWeek.MONDAY, DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        expectedMeetingDays = new ArrayList<>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays, is(expectedMeetingsMondaysAndFridays));
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeekWithInvertedOrder_then_ReturnTrue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        expectedMeetingDays = new ArrayList<>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays, is(expectedMeetingsMondaysAndFridays));
    }

    @Test
    public void when_twoIterators_then_ReturnSameValue() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY);
        Iterator<LocalDate> firstItr = calendar.iterator();
        Iterator<LocalDate> secondItr = calendar.iterator();

        assertEquals(firstItr.next(), secondItr.next());
    }

    @Test
    public void when_twoIterators_then_ReturnSameValues() {
        calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY);
        val firstMeetings = new ArrayList<>();
        List<LocalDate> secondMeetings = new ArrayList<>();

        calendar.iterator().forEachRemaining(firstMeetings::add);
        calendar.iterator().forEachRemaining(secondMeetings::add);
        assertThat(firstMeetings, is(secondMeetings));
    }

    @Test(expected = NoSuchElementException.class)
    public void when_noMeetingsBetweenStartAndEndDate_then_ReturnNoSuchElementException() {
        startDate = LocalDate.of(2018, Month.MARCH, 6);
        endDate = LocalDate.of(2018, Month.MARCH, 9);
        calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY);
        calendar.iterator().next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_endDateIsBeforeStartDate_thenReturn_IllegalArgumentException() {
        startDate = LocalDate.of(2018, Month.MARCH, 1);
        endDate = startDate.minus(30, ChronoUnit.DAYS);
        calendar = new Calendar(startDate, endDate, DayOfWeek.THURSDAY);
    }
}