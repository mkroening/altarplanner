package org.altarplanner.core.domain.massLegacy;

import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.xml.jaxb.util.ServiceTypeCountsXmlAdapter;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@XmlTransient
public abstract class EditableMass extends GenericMass {

    private Map<ServiceType, Integer> serviceTypeCounts;

    EditableMass() {
        super();
        this.serviceTypeCounts = new HashMap<>();
    }

    EditableMass(EditableMass editableMass) {
        super(editableMass);
        this.serviceTypeCounts = editableMass.serviceTypeCounts;
    }

    @XmlJavaTypeAdapter(ServiceTypeCountsXmlAdapter.class)
    public Map<ServiceType, Integer> getServiceTypeCounts() {
        return serviceTypeCounts;
    }

    public void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts) {
        this.serviceTypeCounts = serviceTypeCounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EditableMass that = (EditableMass) o;
        return Objects.equals(serviceTypeCounts, that.serviceTypeCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceTypeCounts);
    }

}
