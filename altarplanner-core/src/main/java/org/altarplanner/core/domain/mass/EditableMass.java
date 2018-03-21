package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;

import java.util.HashMap;
import java.util.Map;

public abstract class EditableMass extends GenericMass {

    private Map<ServiceType, Integer> serviceTypeCount;

    EditableMass() {
        super();
        this.serviceTypeCount = new HashMap<>();
    }

    EditableMass(EditableMass editableMass) {
        super(editableMass);
        this.serviceTypeCount = editableMass.serviceTypeCount;
    }

    public Map<ServiceType, Integer> getServiceTypeCount() {
        return serviceTypeCount;
    }

    public void setServiceTypeCount(Map<ServiceType, Integer> serviceTypeCount) {
        this.serviceTypeCount = serviceTypeCount;
    }

}
