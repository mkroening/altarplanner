package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "discreteMasses"})
public class DiscreteMassCollection {

    private List<ServiceType> serviceTypes;

    private List<DiscreteMass> discreteMasses;

    public DiscreteMassCollection() {
    }

    public DiscreteMassCollection(List<DiscreteMass> discreteMasses) {
        this.discreteMasses = discreteMasses;
        serviceTypes = discreteMasses.parallelStream()
                .flatMap(discreteMass -> discreteMass.getServiceTypeCounts().keySet().parallelStream())
                .distinct()
                .sorted(ServiceType.getDescComparator())
                .collect(Collectors.toUnmodifiableList());
    }

    @XmlElementWrapper(name = "serviceTypes")
    @XmlElement(name = "serviceType")
    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @XmlElementWrapper(name = "discreteMasses")
    @XmlElement(name = "discreteMass")
    public List<DiscreteMass> getDiscreteMasses() {
        return discreteMasses;
    }

    public void setDiscreteMasses(List<DiscreteMass> discreteMasses) {
        this.discreteMasses = discreteMasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscreteMassCollection that = (DiscreteMassCollection) o;
        return Objects.equals(serviceTypes, that.serviceTypes) &&
                Objects.equals(discreteMasses, that.discreteMasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypes, discreteMasses);
    }

}
