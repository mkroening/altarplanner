package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DatedDraftMass;
import org.threeten.extra.LocalDateRange;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "datedDraftMasses"})
public class DiscreteMassCollection {

    private List<ServiceType> serviceTypes;

    private List<DatedDraftMass> discreteMasses;

    /**
     * Noarg public constructor making the class instantiatable for JAXB.
     */
    public DiscreteMassCollection() {
    }

    public DiscreteMassCollection(List<DatedDraftMass> discreteMasses) {
        this.discreteMasses = discreteMasses.stream()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        serviceTypes = discreteMasses.parallelStream()
                .flatMap(discreteMass -> discreteMass.getServiceTypeCounts().keySet().parallelStream())
                .distinct()
                .sorted(ServiceType.getDescComparator())
                .collect(Collectors.toUnmodifiableList());
    }

    public LocalDateRange getDateRange() {
        final LocalDate start = Collections.min(discreteMasses).getDateTime().toLocalDate();
        final LocalDate endInclusive = Collections.max(discreteMasses).getDateTime().toLocalDate();
        return LocalDateRange.ofClosed(start, endInclusive);
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
    public List<DatedDraftMass> getDatedDraftMasses() {
        return discreteMasses;
    }

    public void setDatedDraftMasses(List<DatedDraftMass> discreteMasses) {
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
