package org.altarplanner.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.stream.Stream;

public class DateSpan implements Serializable {

    private LocalDate start;
    private LocalDate end;

    public DateSpan() {
        this.end = this.start = LocalDate.now().plusMonths(1);
    }

    @java.beans.ConstructorProperties({"start", "end"})
    public DateSpan(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public String getDesc() {
        return start.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)) + " - " +
                end.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
    }

    public static Comparator<DateSpan> getDescComparator() {
        return Comparator
                .comparing(DateSpan::getEnd)
                .reversed()
                .thenComparing(DateSpan::getStart);
    }

    public boolean contains(LocalDate date) {
        return (date.compareTo(start) >= 0) == (date.compareTo(end) <= 0);
    }

    public Stream<LocalDate> getDateParallelStream() {
        return start.datesUntil(end.plusDays(1)).parallel();
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

}
