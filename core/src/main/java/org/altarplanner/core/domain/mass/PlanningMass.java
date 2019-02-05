package org.altarplanner.core.domain.mass;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import org.altarplanner.core.domain.planning.Service;
import org.altarplanner.core.domain.ServiceType;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;

@DeepPlanningClone
public class PlanningMass extends DatedMass {

  protected List<Service> services;

  protected boolean pinned;

  /** Noarg public constructor making the class instantiatable for JAXB. */
  public PlanningMass() {}

  public PlanningMass(PlanningMassTemplate planningMassTemplate) {
    super(planningMassTemplate);
    this.services =
        planningMassTemplate.serviceTypeCounts.entrySet().stream()
            .flatMap(
                serviceTypeCount ->
                    IntStream.range(0, serviceTypeCount.getValue())
                        .mapToObj(i -> new Service(this, serviceTypeCount.getKey())))
            .sorted(Comparator.comparing(Service::getType))
            .collect(Collectors.toUnmodifiableList());
    this.pinned = false;
  }

  @XmlElementWrapper(name = "services")
  @XmlElement(name = "service")
  public List<Service> getServices() {
    return services;
  }

  public void setServices(List<Service> services) {
    this.services = services;
  }

  /**
   * Used by {@code services} as {@code @PlanningPin}. Autogenerated by {@code Schedule.load(File)}.
   *
   * @return true if the mass is pinned (no changes allowed)
   */
  @XmlTransient
  public boolean isPinned() {
    return pinned;
  }

  public void setPinned(boolean pinned) {
    this.pinned = pinned;
  }

  /**
   * Used by {@code equals(Object)} and {@code hashCode()} to break the cycle.
   *
   * @return the list of serviceTypes in this mass
   */
  private List<ServiceType> getServiceTypes() {
    return services.stream().map(Service::getType).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PlanningMass that = (PlanningMass) o;
    return pinned == that.pinned && Objects.equals(getServiceTypes(), that.getServiceTypes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getServiceTypes(), pinned);
  }
}
