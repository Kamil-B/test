package common.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Calendar implements Iterable<LocalDate> {

    private LocalDate startDate;
    private LocalDate stopDate;
    private LinkedList<DayOfWeek> meetingDays;

    public Calendar(LocalDate startDate, LocalDate stopDate, DayOfWeek... days) {
        if (startDate.isAfter(stopDate)) {
            throw new IllegalArgumentException("Start date should be before end date !");
        }

        this.startDate = startDate;
        this.stopDate = stopDate;
        sortDays(removeDuplicates(days));
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new CalendarIterator(startDate);
    }

    private class CalendarIterator implements Iterator<LocalDate> {

        private LocalDate nextDate;

        CalendarIterator(LocalDate startDate) {
            this.nextDate = startDate;
        }

        @Override
        public boolean hasNext() {
            return nextDate.with(TemporalAdjusters.next(meetingDays.getFirst()))
                    .isBefore(stopDate) || nextDate.isEqual(stopDate);
        }

        @Override
        public LocalDate next() {
            if (hasNext()) {
                return getNextMeetingDay();
            }
            throw new NoSuchElementException();
        }

        private LocalDate getNextMeetingDay() {
            if (isStartDayTheMeetingDay()) {
                LocalDate firstMeeting = nextDate;
                nextDate = nextDate.plus(1, ChronoUnit.DAYS);
                return firstMeeting;
            }
            nextDate = nextDate.with(TemporalAdjusters.next(meetingDays.pollFirst()));
            meetingDays.addLast(nextDate.getDayOfWeek());
            return nextDate;
        }

        private boolean isStartDayTheMeetingDay() {
            return startDate.equals(nextDate) && meetingDays.contains(startDate.getDayOfWeek());
        }
    }

    private static LinkedList<DayOfWeek> removeDuplicates(DayOfWeek[] days) {
        LinkedList<DayOfWeek> linked = new LinkedList<>();
        linked.addAll(new HashSet<>(Arrays.asList(days)));
        return linked;
    }

    private static DayOfWeek addOneDay(DayOfWeek startDay) {
        if (startDay.getValue() > DayOfWeek.SATURDAY.getValue()) {
            startDay = DayOfWeek.MONDAY;
        } else {
            startDay = startDay.plus(1);
        }
        return startDay;
    }

    private void sortDays(List<DayOfWeek> days) {
        meetingDays = new LinkedList<>();
        DayOfWeek startDay = DayOfWeek.values()[startDate.getDayOfWeek().getValue()];

        while (meetingDays.size() != days.size()) {
            if (days.contains(startDay)) {
                meetingDays.add(startDay);
            }
            startDay = addOneDay(startDay);
        }
    }
}