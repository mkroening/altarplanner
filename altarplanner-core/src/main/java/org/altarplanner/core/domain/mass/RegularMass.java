package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;

public class RegularMass extends EditableMass {

    @Getter @Setter private DayOfWeek day = DayOfWeek.SUNDAY;

    public String getDesc() {
        return day.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " - " +
                getTime() + " - " +
                getChurch();
    }

    public static Comparator<RegularMass> getAlphabeticComparator() {
        return Comparator
                .comparing(RegularMass::getDay)
                .thenComparing(GenericMass::getTime)
                .thenComparing(GenericMass::getChurch)
                .thenComparing(GenericMass::getForm);
    }

}
