package org.altarplanner.core.persistence.jaxb.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.threeten.extra.LocalDateRange;

public class LocalDateRangeXmlAdapter extends XmlAdapter<String, LocalDateRange> {
  @Override
  public LocalDateRange unmarshal(String stringValue) throws Exception {
    return LocalDateRange.parse(stringValue);
  }

  @Override
  public String marshal(LocalDateRange value) throws Exception {
    return value.toString();
  }
}
