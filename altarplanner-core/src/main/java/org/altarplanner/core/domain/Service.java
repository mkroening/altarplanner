package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Service {

    @Getter @Setter private int id;
    @Getter private final PlanningMass mass;
    @Getter private final ServiceType type;
    @PlanningVariable(valueRangeProviderRefs = {"serverRange"})
    @Getter @Setter private Server server;

    public Service(PlanningMass mass, ServiceType type) {
        this.mass = mass;
        this.type = type;
    }

}
