package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.time.LocalDateTime;

public class DateTimeOnRequest extends GenericRequest {

    private final LocalDateTime dateTime;

    public DateTimeOnRequest(Server server, LocalDateTime dateTime) {
        super(server);
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
