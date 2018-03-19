package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.Service;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DeepPlanningClone
public class PlanningMass extends GenericMass {

    @Getter @Setter private List<Service> services;
    @Getter @Setter private LocalDate date;

    public PlanningMass() {
    }

    public PlanningMass(DiscreteMass discreteMass) {
        super(discreteMass);

        this.services = discreteMass.getServiceTypeCount().entrySet().parallelStream()
                .flatMap(serviceTypeCountEntry ->
                        IntStream.range(0, serviceTypeCountEntry.getValue())
                                .mapToObj(value -> new Service(this, serviceTypeCountEntry.getKey())))
                .collect(Collectors.toList());

        this.date = discreteMass.getDate();
    }

    public String serviceDescOf(Server server) {
        Optional<Service> optionalService = services.parallelStream().filter(service -> service.getServer() == server).findAny();
        return optionalService.map(service -> service.getType().getDesc()).orElse(null);
    }

}
