package org.altarplanner.core.planning.domain.state;

import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;
import org.threeten.extra.LocalDateRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScheduleTemplate extends ServerTypeAware implements FeastDayAware {

  private List<PlanningMassTemplate> planningMassTemplates;
  private List<LocalDate> feastDays;

  /** Noarg public constructor making the class instantiatable for JAXB. */
  public ScheduleTemplate() {
    this.planningMassTemplates = new ArrayList<>();
    this.feastDays = new ArrayList<>();
  }

  public ScheduleTemplate(
      List<PlanningMassTemplate> planningMassTemplates, List<LocalDate> feastDays) {
    this.planningMassTemplates =
        planningMassTemplates.stream().sorted().collect(Collectors.toUnmodifiableList());
    super.setServiceTypes(
        planningMassTemplates
            .parallelStream()
            .flatMap(
                planningMassTemplate ->
                    planningMassTemplate.getServiceTypeCounts().keySet().parallelStream())
            .distinct()
            .sorted()
            .collect(Collectors.toUnmodifiableList()));
    this.feastDays = feastDays;
  }

  public ScheduleTemplate(List<PlanningMassTemplate> planningMassTemplates) {
    this(planningMassTemplates, Collections.emptyList());
  }

  public LocalDateRange getDateRange() {
    final LocalDate start = Collections.min(planningMassTemplates).getDateTime().toLocalDate();
    final LocalDate endInclusive =
        Collections.max(planningMassTemplates).getDateTime().toLocalDate();
    return LocalDateRange.ofClosed(start, endInclusive);
  }

  public List<PlanningMassTemplate> getPlanningMassTemplates() {
    return planningMassTemplates;
  }

  public void setPlanningMassTemplates(List<PlanningMassTemplate> planningMassTemplates) {
    this.planningMassTemplates = planningMassTemplates;
  }

  public List<LocalDate> getFeastDays() {
    return feastDays;
  }

  public void setFeastDays(List<LocalDate> feastDays) {
    this.feastDays = feastDays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScheduleTemplate that = (ScheduleTemplate) o;
    return planningMassTemplates.equals(that.planningMassTemplates)
        && feastDays.equals(that.feastDays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(planningMassTemplates, feastDays);
  }
}
