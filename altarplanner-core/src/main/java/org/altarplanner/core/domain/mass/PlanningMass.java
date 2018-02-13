package org.altarplanner.core.domain.mass;

import lombok.Getter;
import org.altarplanner.core.domain.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlanningMass extends GenericMass {

    @Getter private final List<Service> services;
    @Getter private final LocalDate date;

    public PlanningMass(DiscreteMass discreteMass) {
        super(discreteMass);

        this.services = discreteMass.getServiceTypeCount().entrySet().parallelStream()
                .flatMap(serviceTypeCountEntry ->
                        IntStream.range(0, serviceTypeCountEntry.getValue())
                                .mapToObj(value -> new Service(this, serviceTypeCountEntry.getKey())))
                .collect(Collectors.toList());

        this.date = discreteMass.getDate();
    }

}
