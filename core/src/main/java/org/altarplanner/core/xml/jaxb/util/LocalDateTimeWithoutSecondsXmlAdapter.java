package org.altarplanner.core.xml.jaxb.util;

import com.migesok.jaxb.adapter.javatime.TemporalAccessorXmlAdapter;
import org.altarplanner.core.util.DateTimeFormatterUtil;

import java.time.LocalDateTime;

public class LocalDateTimeWithoutSecondsXmlAdapter extends TemporalAccessorXmlAdapter<LocalDateTime> {
    public LocalDateTimeWithoutSecondsXmlAdapter() {
        super(DateTimeFormatterUtil.ISO_LOCAL_DATE_TIME_WITHOUT_SECONDS, LocalDateTime::from);
    }
}
