package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PlanningSolution
public class Schedule {

    @Getter @Setter private DateSpan planningWindow;
    @ValueRangeProvider(id = "serverRange")
    @Getter private final List<Server> servers;
    @Getter private final List<PlanningMass> masses;
    @PlanningScore
    @Getter @Setter private HardSoftScore score;

    public Schedule() {
        this.servers = new ArrayList<>();
        this.masses = new ArrayList<>();
    }

    @PlanningEntityCollectionProperty
    public List<Service> getServices() {
        return masses.parallelStream()
                .flatMap(mass -> mass.getServices().parallelStream())
                .collect(Collectors.toList());
    }

}
