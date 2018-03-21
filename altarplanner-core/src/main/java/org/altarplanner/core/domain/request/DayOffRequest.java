package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.time.DayOfWeek;

public class DayOffRequest extends GenericRequest {

    private final DayOfWeek day;

    public DayOffRequest(Server server, DayOfWeek day) {
        super(server);
        this.day = day;
    }

    public DayOfWeek getDay() {
        return day;
    }

}
