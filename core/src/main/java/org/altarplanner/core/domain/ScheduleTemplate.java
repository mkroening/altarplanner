package org.altarplanner.core.domain;

import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.xml.StrictJAXB;
import org.threeten.extra.LocalDateRange;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "planningMassTemplates", "feastDays"})
public class ScheduleTemplate {

  private List<ServiceType> serviceTypes;

  private List<PlanningMassTemplate> planningMassTemplates;

  private List<LocalDate> feastDays;

  /** Noarg public constructor making the class instantiatable for JAXB. */
  public ScheduleTemplate() {}

  public ScheduleTemplate(
      List<PlanningMassTemplate> planningMassTemplates, List<LocalDate> feastDays) {
    this.planningMassTemplates =
        planningMassTemplates.stream().sorted().collect(Collectors.toUnmodifiableList());
    serviceTypes =
        planningMassTemplates
            .parallelStream()
            .flatMap(
                planningMassTemplate ->
                    planningMassTemplate.getServiceTypeCounts().keySet().parallelStream())
            .distinct()
            .sorted()
            .collect(Collectors.toUnmodifiableList());
    this.feastDays = feastDays;
  }

  public ScheduleTemplate(List<PlanningMassTemplate> planningMassTemplates) {
    this(planningMassTemplates, Collections.emptyList());
  }

  public static ScheduleTemplate unmarshal(Path input) throws UnmarshalException {
    return StrictJAXB.unmarshal(input, ScheduleTemplate.class);
  }

  public void marshal(Path output) {
    StrictJAXB.marshal(this, output);
  }

  public LocalDateRange getDateRange() {
    final LocalDate start = Collections.min(planningMassTemplates).getDateTime().toLocalDate();
    final LocalDate endInclusive =
        Collections.max(planningMassTemplates).getDateTime().toLocalDate();
    return LocalDateRange.ofClosed(start, endInclusive);
  }

  @XmlElementWrapper(name = "serviceTypes")
  @XmlElement(name = "serviceType")
  public List<ServiceType> getServiceTypes() {
    return serviceTypes;
  }

  public void setServiceTypes(List<ServiceType> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }

  @XmlElementWrapper(name = "planningMassTemplates")
  @XmlElement(name = "planningMassTemplate")
  public List<PlanningMassTemplate> getPlanningMassTemplates() {
    return planningMassTemplates;
  }

  public void setPlanningMassTemplates(List<PlanningMassTemplate> planningMassTemplates) {
    this.planningMassTemplates = planningMassTemplates;
  }

  @XmlList
  @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
  public List<LocalDate> getFeastDays() {
    return feastDays;
  }

  public void setFeastDays(List<LocalDate> feastDays) {
    this.feastDays = feastDays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScheduleTemplate that = (ScheduleTemplate) o;
    return Objects.equals(serviceTypes, that.serviceTypes)
        && Objects.equals(planningMassTemplates, that.planningMassTemplates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceTypes, planningMassTemplates);
  }
}
