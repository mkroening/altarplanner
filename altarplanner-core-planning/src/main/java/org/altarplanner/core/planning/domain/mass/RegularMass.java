package org.altarplanner.core.planning.domain.mass;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.altarplanner.core.planning.domain.ServiceType;

public class RegularMass extends BaseMass implements TemplateMass, Comparable<RegularMass> {

  protected DayOfWeek day;

  protected LocalTime time;

  protected Map<ServiceType, Integer> serviceTypeCounts;

  public RegularMass() {
    this.day = DayOfWeek.SUNDAY;
    this.time = LocalTime.of(11, 0);
    this.serviceTypeCounts = new HashMap<>();
  }

  public DayOfWeek getDay() {
    return day;
  }

  public void setDay(DayOfWeek day) {
    this.day = day;
  }

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  @Override
  public Map<ServiceType, Integer> getServiceTypeCounts() {
    return serviceTypeCounts;
  }

  @Override
  public void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts) {
    this.serviceTypeCounts = serviceTypeCounts;
  }

  @Override
  public int compareTo(RegularMass o) {
    return Objects.compare(
        this,
        o,
        Comparator.comparing(RegularMass::getDay)
            .thenComparing(RegularMass::getTime)
            .thenComparing(BASE_COMPARATOR));
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
    RegularMass that = (RegularMass) o;
    return day == that.day
        && Objects.equals(time, that.time)
        && Objects.equals(serviceTypeCounts, that.serviceTypeCounts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), day, time, serviceTypeCounts);
  }
}
