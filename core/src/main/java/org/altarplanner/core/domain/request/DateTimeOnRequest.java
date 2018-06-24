package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.time.LocalDateTime;
import java.util.AbstractMap;

public class DateTimeOnRequest extends AbstractMap.SimpleImmutableEntry<Server, LocalDateTime> {
    public DateTimeOnRequest(Server key, LocalDateTime value) {
        super(key, value);
    }
}
