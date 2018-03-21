package org.altarplanner.core.domain.mass;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;

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

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

}
