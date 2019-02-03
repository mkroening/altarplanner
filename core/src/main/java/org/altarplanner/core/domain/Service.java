package org.altarplanner.core.domain;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.solver.ServerStrengthWeightFactory;
import org.altarplanner.core.solver.ServiceDifficultyWeightFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity(difficultyWeightFactoryClass = ServiceDifficultyWeightFactory.class)
@XmlType(propOrder = {"type", "server"})
public class Service extends AbstractPersistable implements Comparable<Service> {

  private PlanningMass mass;
  private ServiceType type;
  private Server server;

  public Service() {}

  public Service(PlanningMass mass, ServiceType type) {
    this.mass = mass;
    this.type = type;
  }

  @PlanningPin
  public boolean isPinned() {
    return mass.isPinned();
  }

  public String getDesc() {
    return Optional.ofNullable(server).map(Server::getDesc).orElse("Unplanned")
        + " ("
        + type.getName()
        + ")";
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
  @PlanningVariable(
      valueRangeProviderRefs = {"serverRange"},
      strengthWeightFactoryClass = ServerStrengthWeightFactory.class)
  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  @Override
  public String toString() {
    return "Service{" + mass.getDateTime() + ", " + type + ": " + server + "}";
  }

  @Override
  public int compareTo(Service o) {
    return Objects.compare(
        this,
        o,
        Comparator.comparing(Service::getServer, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(Service::getType));
  }

  /**
   * Used by {@code equals(Object)} and {@code hashCode()} to distinct two different services with
   * same type in one mass.
   *
   * @return index of this service in the parent mass
   */
  private int indexInMass() {
    return IntStream.range(0, mass.getServices().size())
        .filter(index -> this == mass.getServices().get(index))
        .findAny()
        .orElseThrow();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Service service = (Service) o;
    return Objects.equals(mass, service.mass)
        && Objects.equals(type, service.type)
        && Objects.equals(indexInMass(), service.indexInMass());
  }

  @Override
  public int hashCode() {
    return Objects.hash(mass, type, indexInMass());
  }
}
