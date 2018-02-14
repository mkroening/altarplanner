package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

import java.time.LocalDateTime;

public class DateTimeOnRequest extends GenericRequest {

    @Getter private final LocalDateTime dateTime;

    public DateTimeOnRequest(Server server, LocalDateTime dateTime) {
        super(server);
        this.dateTime = dateTime;
    }

}
