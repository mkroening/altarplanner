package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.time.LocalDate;
import java.util.AbstractMap;

public class DateOffRequest extends AbstractMap.SimpleImmutableEntry<Server, LocalDate> {
    public DateOffRequest(Server key, LocalDate value) {
        super(key, value);
    }
}
