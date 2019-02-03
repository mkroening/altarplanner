package org.altarplanner.core.xml.jaxb.util;

import com.migesok.jaxb.adapter.javatime.TemporalAccessorXmlAdapter;
import java.time.LocalTime;
import org.altarplanner.core.util.DateTimeFormatterUtil;

public class LocalTimeWithoutSecondsXmlAdapter extends TemporalAccessorXmlAdapter<LocalTime> {
  public LocalTimeWithoutSecondsXmlAdapter() {
    super(DateTimeFormatterUtil.ISO_LOCAL_TIME_WITHOUT_SECONDS, LocalTime::from);
  }
}
