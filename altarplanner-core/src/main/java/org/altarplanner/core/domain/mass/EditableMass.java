package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.xml.ServiceTypeCountAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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

    @XmlJavaTypeAdapter(ServiceTypeCountAdapter.class)
    public Map<ServiceType, Integer> getServiceTypeCount() {
        return serviceTypeCount;
    }

    public void setServiceTypeCount(Map<ServiceType, Integer> serviceTypeCount) {
        this.serviceTypeCount = serviceTypeCount;
    }

}
