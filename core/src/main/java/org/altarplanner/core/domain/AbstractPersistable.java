package org.altarplanner.core.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

public abstract class AbstractPersistable implements Serializable {

    protected Integer planningId;

    protected AbstractPersistable() {
    }

    @PlanningId
    @XmlTransient
    public Integer getPlanningId() {
        return planningId;
    }

    public void setPlanningId(Integer planningId) {
        this.planningId = planningId;
    }

}
