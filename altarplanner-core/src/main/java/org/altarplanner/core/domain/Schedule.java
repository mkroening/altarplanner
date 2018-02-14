package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.mass.PlanningMass;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    @Getter @Setter private DateSpan planningWindow;
    @Getter private final List<Server> servers;
    @Getter private final List<PlanningMass> masses;

    public Schedule() {
        this.servers = new ArrayList<>();
        this.masses = new ArrayList<>();
    }

}
