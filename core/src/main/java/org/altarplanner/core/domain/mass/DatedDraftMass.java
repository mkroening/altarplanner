package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;

import java.util.Map;
import java.util.Objects;

public class DatedDraftMass extends DatedMass implements DraftMass {

    protected Map<ServiceType, Integer> serviceTypeCounts = Map.of();

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
