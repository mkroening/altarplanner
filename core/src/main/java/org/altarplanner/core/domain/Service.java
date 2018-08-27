package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.solver.ServerStrengthWeightFactory;
import org.altarplanner.core.solver.ServiceDifficultyWeightFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

@PlanningEntity(difficultyWeightFactoryClass = ServiceDifficultyWeightFactory.class)
@XmlType(propOrder = {"server", "type"})
public class Service implements Serializable {

    private int id;
    private PlanningMass mass;
    private ServiceType type;
    private Server server;

    public Service() {
    }

    public Service(PlanningMass mass, ServiceType type) {
        this.mass = mass;
        this.type = type;
    }

    @PlanningPin
    public boolean isPinned() {
        return mass.isPinned();
    }

    public String getDesc() {
        return server.getDesc() + " (" + type.getName() + ")";
    }

    public static Comparator<Service> getDescComparator() {
        return Comparator
                .comparing(Service::getServer, Server.getDescComparator())
                .thenComparing(service -> service.getType().getName());
    }

    @XmlTransient
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlTransient
    public PlanningMass getMass() {
        return mass;
    }

    public void setMass(PlanningMass mass) {
        this.mass = mass;
    }

    @XmlIDREF
    @XmlAttribute
    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    @XmlIDREF
    @XmlAttribute
    @PlanningVariable(valueRangeProviderRefs = {"serverRange"}, strengthWeightFactoryClass = ServerStrengthWeightFactory.class)
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return id == service.id &&
                Objects.equals(mass, service.mass) &&
                Objects.equals(type, service.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mass, type);
    }

    @Override
    public String toString() {
        return "Service{" +
                LocalDateTime.of(mass.getDate(), mass.getTime()) +
                ", " + type +
                ": " + server +
                "}";
    }

}
