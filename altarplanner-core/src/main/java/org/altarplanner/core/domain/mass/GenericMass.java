package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.Config;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

public abstract class GenericMass implements Serializable {

    @Getter @Setter private LocalTime time;
    @Getter @Setter private String church;
    @Getter @Setter private String form;

    GenericMass() {
        this.time = LocalTime.of(11, 0);
        this.church = Config.RESOURCE_BUNDLE.getString("mass.church");
        this.form = Config.RESOURCE_BUNDLE.getString("mass.form");
    }

    GenericMass(GenericMass genericMass) {
        this.time = genericMass.time;
        this.church = genericMass.church;
        this.form = genericMass.form;
    }

    public String getGenericDesc() {
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " - " +
                church;
    }

    public static Comparator<GenericMass> getGenericDescComparator() {
        return Comparator
                .comparing(GenericMass::getTime)
                .thenComparing(GenericMass::getChurch)
                .thenComparing(GenericMass::getForm);
    }

}
