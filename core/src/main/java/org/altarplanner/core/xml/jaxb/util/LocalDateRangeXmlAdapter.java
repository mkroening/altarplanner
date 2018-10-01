package org.altarplanner.core.xml.jaxb.util;

import org.threeten.extra.LocalDateRange;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
