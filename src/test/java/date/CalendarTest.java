package date;

import node.date.Calendar;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarTest {

    private List<LocalDate> expectedMeetingsMondaysAndFridays;

    @Before
    public void setUp() {
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
    public void when_getFirstMeetingWhenInSameDayAsStartDate_thenReturnTrue() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.plus(30, ChronoUnit.DAYS);
        LocalDate actualDate = new Calendar(startDate, endDate, DayOfWeek.THURSDAY).iterator().next();

        assertThat(actualDate).isEqualTo(startDate);
    }

    @Test
    public void when_getAllMeetingsForOneMeetingInWeekStartAtSameDay_then_ReturnTrue() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.plus(30, ChronoUnit.DAYS);

        val calendar = new Calendar(startDate, endDate, DayOfWeek.THURSDAY);
        val expectedMeetingDays = new ArrayList<LocalDate>();
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 1));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 8));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 15));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 22));
        expectedMeetingDays.add(LocalDate.of(2018, Month.MARCH, 29));

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays).isEqualTo(expectedMeetingDays);
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeek_then_ReturnTrue() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.plus(30, ChronoUnit.DAYS);
        val calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY, DayOfWeek.MONDAY);
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays).isEqualTo(expectedMeetingsMondaysAndFridays);
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeekWhenDuplications_then_ReturnTrue() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.plus(30, ChronoUnit.DAYS);

        val calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY, DayOfWeek.MONDAY,
                DayOfWeek.MONDAY, DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays).isEqualTo(expectedMeetingsMondaysAndFridays);
    }

    @Test
    public void when_getAllMeetingsForTwoMeetingsInWeekWithInvertedOrder_then_ReturnTrue() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.plus(30, ChronoUnit.DAYS);

        val calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        List<LocalDate> actualMeetingsDays = new ArrayList<>();

        calendar.iterator().forEachRemaining(actualMeetingsDays::add);
        assertThat(actualMeetingsDays).isEqualTo(expectedMeetingsMondaysAndFridays);
    }

    @Test
    public void when_twoIterators_then_ReturnSameValue() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.plus(30, ChronoUnit.DAYS);

        val calendar = new Calendar(startDate, endDate, DayOfWeek.FRIDAY);
        Iterator<LocalDate> firstItr = calendar.iterator();
        Iterator<LocalDate> secondItr = calendar.iterator();

        assertThat(firstItr.next()).isEqualTo(secondItr.next());
    }

    @Test(expected = NoSuchElementException.class)
    public void when_noMeetingsBetweenStartAndEndDate_then_NoSuchElementExceptionThrown() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 6);
        LocalDate endDate = LocalDate.of(2018, Month.MARCH, 9);
        val calendar = new Calendar(startDate, endDate, DayOfWeek.MONDAY);
        calendar.iterator().next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_endDateIsBeforeStartDate_then_IllegalArgumentExceptionThrown() {
        LocalDate startDate = LocalDate.of(2018, Month.MARCH, 1);
        LocalDate endDate = startDate.minus(30, ChronoUnit.DAYS);
        new Calendar(startDate, endDate, DayOfWeek.THURSDAY);
    }
}