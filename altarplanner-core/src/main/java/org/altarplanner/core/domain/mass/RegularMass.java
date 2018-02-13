package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

public class RegularMass extends EditableMass {

    @Getter @Setter private DayOfWeek day = DayOfWeek.SUNDAY;

}
