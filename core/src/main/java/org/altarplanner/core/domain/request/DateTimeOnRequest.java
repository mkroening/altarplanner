package org.altarplanner.core.domain.request;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import org.altarplanner.core.domain.Server;

public class DateTimeOnRequest extends AbstractMap.SimpleImmutableEntry<Server, LocalDateTime> {
  public DateTimeOnRequest(Server key, LocalDateTime value) {
    super(key, value);
  }
}
