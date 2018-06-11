package org.altarplanner.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.stream.Stream;

public final class DateSpan implements Serializable {

    private final LocalDate start;
    private final LocalDate end;

    public static DateSpan of(LocalDate start, LocalDate end) {
        return new DateSpan(start, end);
    }

    private DateSpan(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public String format(DateTimeFormatter formatter) {
        return start.format(formatter) + " - " + end.format(formatter);
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

    public Stream<LocalDate> stream() {
        return start.datesUntil(end.plusDays(1));
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

}
