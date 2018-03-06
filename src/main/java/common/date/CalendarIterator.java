package common.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class CalendarIterator implements Iterator<LocalDate> {

    private LocalDate actualDate;
    private LocalDate stopDate;
    private LocalDate startDate;
    private LinkedList<DayOfWeek> meetingDays;

    CalendarIterator(LocalDate startDate, LocalDate stopDate, LinkedList<DayOfWeek> meetingDays) {
        this.stopDate = stopDate;
        this.startDate = startDate;
        this.actualDate = startDate;
        this.meetingDays = meetingDays;
    }

    @Override
    public boolean hasNext() {
        return actualDate.with(TemporalAdjusters.next(meetingDays.getFirst()))
                .isBefore(stopDate) || actualDate.isEqual(stopDate);
    }

    @Override
    public LocalDate next() {
        if (hasNext()) {
            if (isStartDayTheMeetingDay()) {
                return actualDate;
            }
            actualDate = actualDate.with(TemporalAdjusters.next(meetingDays.pollFirst()));
            meetingDays.addLast(actualDate.getDayOfWeek());
            return actualDate;
        }
        throw new NoSuchElementException();
    }

    private boolean isStartDayTheMeetingDay() {
        return startDate.isEqual(actualDate) && meetingDays.contains(startDate.getDayOfWeek());
    }
}