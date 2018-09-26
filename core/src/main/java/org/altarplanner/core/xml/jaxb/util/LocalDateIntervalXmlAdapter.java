package org.altarplanner.core.xml.jaxb.util;

import org.altarplanner.core.util.LocalDateInterval;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateIntervalXmlAdapter extends XmlAdapter<String, LocalDateInterval> {
    @Override
    public LocalDateInterval unmarshal(String stringValue) throws Exception {
        return LocalDateInterval.parse(stringValue);
    }

    @Override
    public String marshal(LocalDateInterval value) throws Exception {
        return value.toString();
    }
}
