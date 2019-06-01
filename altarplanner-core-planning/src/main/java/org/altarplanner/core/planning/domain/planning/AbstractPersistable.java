package org.altarplanner.core.planning.domain.planning;

import java.io.Serializable;
import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class AbstractPersistable implements Serializable {

  protected Integer planningId;

  protected AbstractPersistable() {}

  @PlanningId
  public Integer getPlanningId() {
    return planningId;
  }

  public void setPlanningId(Integer planningId) {
    this.planningId = planningId;
  }
}
