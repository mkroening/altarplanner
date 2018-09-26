package org.altarplanner.core.util;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public final class LocalDateInterval implements Comparable<LocalDateInterval>, Serializable {

    public final static String DELIMITER_SOLIDUS = "/";
    public final static String DELIMITER_DOUBLE_HYPHEN = "--";

    private final LocalDate start;
    private final LocalDate end;

    public static LocalDateInterval of(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (end.isBefore(start))
            throw new DateTimeException("End date can not be before start date.");
        return new LocalDateInterval(start, end);
    }

    private LocalDateInterval(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public String format(DateTimeFormatter formatter, String delimiter) {
        return start.format(formatter) + delimiter + end.format(formatter);
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

    @Override
    public int compareTo(LocalDateInterval o) {
        return Objects.compare(this, o, Comparator
                .comparing(LocalDateInterval::getEnd)
                .thenComparing(LocalDateInterval::getStart)
                .reversed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDateInterval that = (LocalDateInterval) o;
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    private String toString(final String delimiter) {
        if (start.getYear() != end.getYear()) {
            return start + delimiter + end;
        }
        if (start.getMonth() != end.getMonth()) {
            return start + delimiter + end.toString().substring(5);
        }
        if (start.getDayOfMonth() != end.getDayOfMonth()) {
            return start + delimiter + end.toString().substring(8);
        }
        return start + delimiter;
    }

    @Override
    public String toString() {
        return toString(DELIMITER_SOLIDUS);
    }

    public String toHyphenString() {
        return toString(DELIMITER_DOUBLE_HYPHEN);
    }

}
