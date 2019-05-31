package org.altarplanner.core.planning.xml.jaxb.util;

import java.time.LocalTime;

import io.github.threetenjaxb.core.TemporalAccessorXmlAdapter;
import org.altarplanner.core.planning.util.DateTimeFormatterUtil;

public class LocalTimeWithoutSecondsXmlAdapter extends TemporalAccessorXmlAdapter<LocalTime> {
  public LocalTimeWithoutSecondsXmlAdapter() {
    super(DateTimeFormatterUtil.ISO_LOCAL_TIME_WITHOUT_SECONDS, LocalTime::from);
  }
}
