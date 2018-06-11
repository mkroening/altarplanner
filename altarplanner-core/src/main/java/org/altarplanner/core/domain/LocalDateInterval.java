package org.altarplanner.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public final class LocalDateInterval implements Serializable {

    private final LocalDate start;
    private final LocalDate end;

    public static Comparator<LocalDateInterval> getRecencyComparator() {
        return Comparator
                .comparing(LocalDateInterval::getEnd)
                .reversed()
                .thenComparing(LocalDateInterval::getStart);
    }

    public static LocalDateInterval of(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        return new LocalDateInterval(start, end);
    }

    private LocalDateInterval(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(LocalDate date) {
        return (date.compareTo(start) >= 0) == (date.compareTo(end) <= 0);
    }

    public String format(DateTimeFormatter formatter) {
        return start.format(formatter) + " - " + end.format(formatter);
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
