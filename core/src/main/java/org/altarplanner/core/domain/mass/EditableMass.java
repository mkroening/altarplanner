package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.xml.jaxb.util.ServiceTypeCountXmlAdapter;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EditableMass that = (EditableMass) o;
        return Objects.equals(serviceTypeCount, that.serviceTypeCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceTypeCount);
    }

}
