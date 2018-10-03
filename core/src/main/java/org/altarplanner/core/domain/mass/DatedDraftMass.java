package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class DatedDraftMass extends DatedMass implements DraftMass {

    protected Map<ServiceType, Integer> serviceTypeCounts;

    public DatedDraftMass() {
        this.serviceTypeCounts = Map.of();
    }

    public DatedDraftMass(RegularMass regularMass, LocalDate date) {
        super(regularMass, LocalDateTime.of(date, regularMass.time));
        this.serviceTypeCounts = Map.copyOf(regularMass.serviceTypeCounts);
    }

    @Override
    public Map<ServiceType, Integer> getServiceTypeCounts() {
        return serviceTypeCounts;
    }

    @Override
    public void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts) {
        this.serviceTypeCounts = serviceTypeCounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DatedDraftMass that = (DatedDraftMass) o;
        return Objects.equals(serviceTypeCounts, that.serviceTypeCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceTypeCounts);
    }
}
