package org.altarplanner.core.planning.xml.jaxb.util;

import java.time.LocalDateTime;

import io.github.threetenjaxb.core.TemporalAccessorXmlAdapter;
import org.altarplanner.core.planning.util.DateTimeFormatterUtil;

public class LocalDateTimeWithoutSecondsXmlAdapter
    extends TemporalAccessorXmlAdapter<LocalDateTime> {
  public LocalDateTimeWithoutSecondsXmlAdapter() {
    super(DateTimeFormatterUtil.ISO_LOCAL_DATE_TIME_WITHOUT_SECONDS, LocalDateTime::from);
  }
}
