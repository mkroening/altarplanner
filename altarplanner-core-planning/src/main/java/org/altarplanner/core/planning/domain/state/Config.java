package org.altarplanner.core.planning.domain.state;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.planning.domain.mass.RegularMass;
import org.threeten.extra.LocalDateRange;

public class Config extends ServerAware implements Serializable {

  public static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle("org.altarplanner.core.locale.locale");

  private List<RegularMass> regularMasses;

  public Config() {
    super();
    this.regularMasses = new ArrayList<>();
  }

  public Config(Config other) {
    super(other);
    this.regularMasses = other.regularMasses;
  }

  public Stream<PlanningMassTemplate> getPlanningMassTemplateStreamFromRegularMassesIn(
      LocalDateRange dateRange) {
    Map<DayOfWeek, List<RegularMass>> dayMassMap =
        regularMasses.stream().collect(Collectors.groupingBy(RegularMass::getDay));
    return dateRange.stream()
        .flatMap(
            date ->
                Optional.ofNullable(dayMassMap.get(date.getDayOfWeek())).stream()
                    .flatMap(
                        masses ->
                            masses.stream().map(mass -> new PlanningMassTemplate(mass, date))));
  }

  public void remove(final ServiceType serviceType) {
    super.remove(serviceType);
    regularMasses.forEach(regularMass -> regularMass.getServiceTypeCounts().remove(serviceType));
  }

  public List<RegularMass> getRegularMasses() {
    return regularMasses;
  }

  public void setRegularMasses(List<RegularMass> regularMasses) {
    this.regularMasses = regularMasses;
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
    Config config = (Config) o;
    return regularMasses.equals(config.regularMasses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), regularMasses);
  }
}
