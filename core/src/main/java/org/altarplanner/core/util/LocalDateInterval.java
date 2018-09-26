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
        Objects.requireNonNull(start, "Start date must not be null");
        Objects.requireNonNull(end, "End date must not be null");
        if (end.isBefore(start))
            throw new DateTimeException("End date can not be before start date.");
        return new LocalDateInterval(start, end);
    }

    public static LocalDateInterval parse(String text, DateTimeFormatter formatter, String delimiter) {
        final int delimiterIndex = text.indexOf(delimiter);
        final String startText = text.substring(0, delimiterIndex);
        final LocalDate start = LocalDate.parse(startText, formatter);
        String tmpEndText = text.substring(delimiterIndex + delimiter.length());
        if (DateTimeFormatter.ISO_LOCAL_DATE.equals(formatter)) {
            tmpEndText = text.substring(0, startText.length() - tmpEndText.length()) + tmpEndText;
        }
        final String endText = tmpEndText;
        final LocalDate end = LocalDate.parse(endText, formatter);
        return LocalDateInterval.of(start, end);
    }

    public static LocalDateInterval parse(String text) {
        return LocalDateInterval.parse(text, DateTimeFormatter.ISO_LOCAL_DATE, DELIMITER_SOLIDUS);
    }

    public static LocalDateInterval parseHyphen(String text) {
        return LocalDateInterval.parse(text, DateTimeFormatter.ISO_LOCAL_DATE, DELIMITER_DOUBLE_HYPHEN);
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

    public Stream<LocalDate> dates() {
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
            return start + delimiter + end.toString().substring(4 + 1);
        }
        if (start.getDayOfMonth() != end.getDayOfMonth()) {
            return start + delimiter + end.toString().substring(4 + 1 + 2 + 1);
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
