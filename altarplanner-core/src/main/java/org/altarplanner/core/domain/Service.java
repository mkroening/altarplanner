package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.solver.MovableServiceSelectionFilter;
import org.altarplanner.core.solver.ServiceDifficultyWeightFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.io.Serializable;
import java.util.Comparator;

@PlanningEntity(movableEntitySelectionFilter = MovableServiceSelectionFilter.class,
        difficultyWeightFactoryClass = ServiceDifficultyWeightFactory.class)
public class Service implements Serializable {

    private int id;
    private PlanningMass mass;
    private ServiceType type;
    @PlanningVariable(valueRangeProviderRefs = {"serverRange"})
    private Server server;

    public Service() {
    }

    public Service(PlanningMass mass, ServiceType type) {
        this.mass = mass;
        this.type = type;
    }

    public String getDesc() {
        return server.getDesc() + " (" + type.getName() + ")";
    }

    public static Comparator<Service> getDescComparator() {
        return Comparator
                .comparing(Service::getServer, Server.getDescComparator())
                .thenComparing(service -> service.getType().getName());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlanningMass getMass() {
        return mass;
    }

    public void setMass(PlanningMass mass) {
        this.mass = mass;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

}
