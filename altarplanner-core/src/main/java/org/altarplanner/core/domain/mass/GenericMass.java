package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.Config;

import java.io.Serializable;
import java.time.LocalTime;

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

}
