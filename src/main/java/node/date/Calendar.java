package node.date;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
public class Calendar implements Iterable<LocalDate> {

    private LocalDate startDate;
    private LocalDate stopDate;
    private Queue<DayOfWeek> meetingDays;

    public Calendar(LocalDate startDate, LocalDate stopDate, DayOfWeek... days) {
        if (startDate.isAfter(stopDate)) {
            throw new IllegalArgumentException("Start date should be before end date !");
        }
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.meetingDays = sortDays(removeDuplicates(days));
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new CalendarIterator();
    }

    private class CalendarIterator implements Iterator<LocalDate> {

        // next returned date or null if no date can be returned
        private LocalDate nextDate;

        CalendarIterator() {
            this.nextDate = isMeetingDay(startDate) ? startDate : getNextMeetingDay(startDate);
        }

        @Override
        public boolean hasNext() {
            return nextDate != null;
        }

        @Override
        public LocalDate next() {
            if (nextDate == null) {
                throw new NoSuchElementException();
            }
            LocalDate actualDate = nextDate;
            nextDate = getNextMeetingDay(nextDate);
            return actualDate;
        }

        private LocalDate getNextMeetingDay(LocalDate date) {
            LocalDate nextDate = date.with(TemporalAdjusters.next(meetingDays.poll()));
            meetingDays.add(nextDate.getDayOfWeek());
            if (nextDate.isAfter(stopDate))
                return null;
            return nextDate;
        }

        private boolean isMeetingDay(LocalDate startDate) {
            return meetingDays.contains(startDate.getDayOfWeek());
        }
    }

    private Queue<DayOfWeek> sortDays(Queue<DayOfWeek> days) {
        Queue<DayOfWeek> meetingDays = new LinkedList<>();
        DayOfWeek startDay = DayOfWeek.values()[startDate.getDayOfWeek().getValue()];

        while (meetingDays.size() != days.size()) {
            if (days.contains(startDay)) {
                meetingDays.add(startDay);
            }
            startDay = shiftByOneDay(startDay);
        }
        return meetingDays;
    }

    private static Queue<DayOfWeek> removeDuplicates(DayOfWeek[] days) {
        return new LinkedList<>(new HashSet<>(Arrays.asList(days)));
    }

    private static DayOfWeek shiftByOneDay(DayOfWeek startDay) {
        if (startDay.getValue() > DayOfWeek.SATURDAY.getValue()) {
            return DayOfWeek.MONDAY;
        }
        return startDay.plus(1);
    }
}