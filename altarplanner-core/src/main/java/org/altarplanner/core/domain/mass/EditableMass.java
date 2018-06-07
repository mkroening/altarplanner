package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.xml.jaxb.util.ServiceTypeCountXmlAdapter;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlTransient
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

    @XmlJavaTypeAdapter(ServiceTypeCountXmlAdapter.class)
    public Map<ServiceType, Integer> getServiceTypeCount() {
        return serviceTypeCount;
    }

    public void setServiceTypeCount(Map<ServiceType, Integer> serviceTypeCount) {
        this.serviceTypeCount = serviceTypeCount;
    }

}
