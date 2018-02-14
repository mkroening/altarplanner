package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

import java.time.DayOfWeek;

public class DayOffRequest extends GenericRequest {

    @Getter private final DayOfWeek day;

    public DayOffRequest(Server server, DayOfWeek day) {
        super(server);
        this.day = day;
    }

}
