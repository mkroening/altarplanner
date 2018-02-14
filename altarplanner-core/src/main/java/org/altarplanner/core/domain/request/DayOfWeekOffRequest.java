package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

import java.time.DayOfWeek;

public class DayOfWeekOffRequest extends GenericRequest {

    @Getter private final DayOfWeek day;

    public DayOfWeekOffRequest(Server server, DayOfWeek day) {
        super(server);
        this.day = day;
    }

}
