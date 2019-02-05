package org.altarplanner.core.domain.planning;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class AbstractPersistable implements Serializable {

  protected Integer planningId;

  protected AbstractPersistable() {}

  @PlanningId
  @XmlTransient
  public Integer getPlanningId() {
    return planningId;
  }

  public void setPlanningId(Integer planningId) {
    this.planningId = planningId;
  }
}
