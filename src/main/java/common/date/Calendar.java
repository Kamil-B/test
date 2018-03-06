package common.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Calendar implements Iterable<LocalDate> {

    private LocalDate startDate;
    private LocalDate stopDate;
    private LinkedList<DayOfWeek> meetingDays;

    public Calendar(LocalDate startDate, LocalDate stopDate, DayOfWeek... days) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.meetingDays = new LinkedList<>(Arrays.asList(days));
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new CalendarIterator(startDate, stopDate, meetingDays);
    }
}