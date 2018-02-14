package org.altarplanner.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.stream.Stream;

@AllArgsConstructor
public class DateSpan {

    @Getter @Setter private LocalDate start;
    @Getter @Setter private LocalDate end;

    public DateSpan() {
        this.end = this.start = LocalDate.now().plusMonths(1);
    }

    public boolean contains(LocalDate date) {
        return (date.compareTo(start) >= 0) == (date.compareTo(end) <= 0);
    }

    public Stream<LocalDate> getDateParallelStream() {
        return start.datesUntil(end.plusDays(1)).parallel();
    }

}
