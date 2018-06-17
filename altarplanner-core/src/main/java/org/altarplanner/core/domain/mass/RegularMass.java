package org.altarplanner.core.domain.mass;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

@XmlType(propOrder = {"day", "time", "church", "form", "serviceTypeCount"})
public class RegularMass extends EditableMass {

    private DayOfWeek day = DayOfWeek.SUNDAY;

    public String getDesc() {
        return day.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " - " +
                getTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " - " +
                getChurch();
    }

    public static Comparator<RegularMass> getDescComparator() {
        return Comparator
                .comparing(RegularMass::getDay)
                .thenComparing(GenericMass::getTime)
                .thenComparing(GenericMass::getChurch)
                .thenComparing(GenericMass::getForm);
    }

    @XmlAttribute
    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularMass that = (RegularMass) o;
        return day == that.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }

}
