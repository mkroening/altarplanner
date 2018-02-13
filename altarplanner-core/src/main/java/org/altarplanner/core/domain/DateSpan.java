package org.altarplanner.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
public class DateSpan {

    @Getter @Setter private LocalDate start;
    @Getter @Setter private LocalDate end;

    public DateSpan() {
        this.end = this.start = LocalDate.now().plusMonths(1);
    }

}
