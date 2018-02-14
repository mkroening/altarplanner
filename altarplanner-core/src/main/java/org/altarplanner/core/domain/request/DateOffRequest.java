package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

import java.time.LocalDate;

public class DateOffRequest extends GenericRequest {

    @Getter private final LocalDate date;

    public DateOffRequest(Server server, LocalDate date) {
        super(server);
        this.date = date;
    }

}
