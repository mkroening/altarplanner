package org.altarplanner.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.stream.Stream;

@AllArgsConstructor
public class DateSpan implements Serializable {

    @Getter @Setter private LocalDate start;
    @Getter @Setter private LocalDate end;

    public DateSpan() {
        this.end = this.start = LocalDate.now().plusMonths(1);
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

}
