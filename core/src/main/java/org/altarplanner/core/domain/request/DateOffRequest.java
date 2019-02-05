package org.altarplanner.core.domain.request;

import java.time.LocalDate;
import java.util.AbstractMap;
import org.altarplanner.core.domain.planning.Server;

public class DateOffRequest extends AbstractMap.SimpleImmutableEntry<Server, LocalDate> {
  public DateOffRequest(Server key, LocalDate value) {
    super(key, value);
  }
}
