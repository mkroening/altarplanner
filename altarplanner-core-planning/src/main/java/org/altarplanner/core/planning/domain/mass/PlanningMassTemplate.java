package org.altarplanner.core.planning.domain.mass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.xml.jaxb.util.ServiceTypeCountsXmlAdapter;

public class PlanningMassTemplate extends DatedMass implements TemplateMass {

  protected Map<ServiceType, Integer> serviceTypeCounts;

  public PlanningMassTemplate() {
    this.serviceTypeCounts = new HashMap<>();
  }

  public PlanningMassTemplate(RegularMass regularMass, LocalDate date) {
    super(regularMass, LocalDateTime.of(date, regularMass.time));
    this.serviceTypeCounts = new HashMap<>(regularMass.serviceTypeCounts);
  }

  @XmlJavaTypeAdapter(ServiceTypeCountsXmlAdapter.class)
  @Override
  public Map<ServiceType, Integer> getServiceTypeCounts() {
    return serviceTypeCounts;
  }

  @Override
  public void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts) {
    this.serviceTypeCounts = serviceTypeCounts;
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
    PlanningMassTemplate that = (PlanningMassTemplate) o;
    return Objects.equals(serviceTypeCounts, that.serviceTypeCounts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), serviceTypeCounts);
  }
}
