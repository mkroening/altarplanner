package org.altarplanner.core.planning.domain.mass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

public abstract class DatedMass extends BaseMass implements Comparable<DatedMass> {

  protected LocalDateTime dateTime;

  protected DatedMass() {
    this.dateTime = LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.of(11, 0));
  }

  protected DatedMass(BaseMass baseMass, LocalDateTime dateTime) {
    super(baseMass);
    this.dateTime = dateTime;
  }

  protected DatedMass(DatedMass datedMass) {
    this(datedMass, datedMass.dateTime);
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public int compareTo(DatedMass o) {
    return Objects.compare(
        this, o, Comparator.comparing(DatedMass::getDateTime).thenComparing(BASE_COMPARATOR));
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
    DatedMass datedMass = (DatedMass) o;
    return Objects.equals(dateTime, datedMass.dateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), dateTime);
  }
}
