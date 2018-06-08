package org.altarplanner.core.xml.jaxb.util;

import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.mass.DiscreteMass;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
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
        serviceTypes = discreteMasses.parallelStream().flatMap(discreteMass -> discreteMass.getServiceTypeCount().keySet().parallelStream()).distinct().collect(Collectors.toList());
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

}
