package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.ServiceType;

import java.util.HashMap;
import java.util.Map;

abstract class EditableMass extends GenericMass {

    @Getter @Setter private Map<ServiceType, Integer> serviceTypeCount;

    EditableMass() {
        super();
        this.serviceTypeCount = new HashMap<>();
    }

    EditableMass(EditableMass editableMass) {
        super(editableMass);
        this.serviceTypeCount = editableMass.getServiceTypeCount();
    }

}
