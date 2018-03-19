package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.solver.MovableServiceSelectionFilter;
import org.altarplanner.core.solver.ServiceDifficultyWeightFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.io.Serializable;

@PlanningEntity(movableEntitySelectionFilter = MovableServiceSelectionFilter.class,
        difficultyWeightFactoryClass = ServiceDifficultyWeightFactory.class)
public class Service implements Serializable {

    @Getter @Setter private int id;
    @Getter @Setter private PlanningMass mass;
    @Getter @Setter private ServiceType type;
    @PlanningVariable(valueRangeProviderRefs = {"serverRange"})
    @Getter @Setter private Server server;

    public Service() {
    }

    public Service(PlanningMass mass, ServiceType type) {
        this.mass = mass;
        this.type = type;
    }

}
