package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.time.DayOfWeek;
import java.util.AbstractMap;

public class DayOffRequest extends AbstractMap.SimpleImmutableEntry<Server, DayOfWeek> {
    public DayOffRequest(Server key, DayOfWeek value) {
        super(key, value);
    }
}
