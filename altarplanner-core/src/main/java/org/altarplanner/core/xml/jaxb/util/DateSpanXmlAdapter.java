package org.altarplanner.core.xml.jaxb.util;

import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter;
import org.altarplanner.core.domain.DateSpan;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

public class DateSpanXmlAdapter extends XmlAdapter<DateSpanXmlAdapter.AdaptedDateSpan, DateSpan> {
    static class AdaptedDateSpan {
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        @XmlAttribute
        private LocalDate start;
        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
        @XmlAttribute
        private LocalDate end;
    }

    @Override
    public DateSpan unmarshal(AdaptedDateSpan v) {
        return DateSpan.of(v.start, v.end);
    }

    @Override
    public AdaptedDateSpan marshal(DateSpan v) {
        AdaptedDateSpan adaptedDateSpan = new AdaptedDateSpan();
        adaptedDateSpan.start = v.getStart();
        adaptedDateSpan.end = v.getEnd();
        return adaptedDateSpan;
    }
}
