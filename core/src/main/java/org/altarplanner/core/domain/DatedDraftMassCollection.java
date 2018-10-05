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
public class DatedDraftMassCollection {

    private List<ServiceType> serviceTypes;

    private List<DatedDraftMass> datedDraftMasses;

    /**
     * Noarg public constructor making the class instantiatable for JAXB.
     */
    public DatedDraftMassCollection() {
    }

    public DatedDraftMassCollection(List<DatedDraftMass> datedDraftMasses) {
        this.datedDraftMasses = datedDraftMasses.stream()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        serviceTypes = datedDraftMasses.parallelStream()
                .flatMap(datedDraftMass -> datedDraftMass.getServiceTypeCounts().keySet().parallelStream())
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    public LocalDateRange getDateRange() {
        final LocalDate start = Collections.min(datedDraftMasses).getDateTime().toLocalDate();
        final LocalDate endInclusive = Collections.max(datedDraftMasses).getDateTime().toLocalDate();
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

    @XmlElementWrapper(name = "datedDraftMasses")
    @XmlElement(name = "datedDraftMass")
    public List<DatedDraftMass> getDatedDraftMasses() {
        return datedDraftMasses;
    }

    public void setDatedDraftMasses(List<DatedDraftMass> datedDraftMasses) {
        this.datedDraftMasses = datedDraftMasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatedDraftMassCollection that = (DatedDraftMassCollection) o;
        return Objects.equals(serviceTypes, that.serviceTypes) &&
                Objects.equals(datedDraftMasses, that.datedDraftMasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypes, datedDraftMasses);
    }

}
