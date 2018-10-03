package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.Service;
import org.altarplanner.core.domain.ServiceType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlanningMass extends DatedMass {

    protected List<Service> services;

    protected boolean pinned;

    public PlanningMass() {
        this.services = List.of();
        this.pinned = false;
    }

    public PlanningMass(DatedDraftMass datedDraftMass) {
        super(datedDraftMass);
        this.services = datedDraftMass.serviceTypeCounts.entrySet().stream()
                .flatMap(serviceTypeCount -> IntStream.range(0, serviceTypeCount.getValue())
                        .mapToObj(i -> new Service())) // TODO: integrate with Service
                .sorted(Comparator.comparing(Service::getType, ServiceType.getDescComparator()))
                .collect(Collectors.toUnmodifiableList());
        this.pinned = false;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    /**
     * Used by {@code equals(Object)} and {@code hashCode()} to break the cycle.
     * @return the list of serviceTypes in this mass
     */
    private List<ServiceType> getServiceTypes() {
        return services.stream().map(Service::getType).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Used by {@code equals(Object)} and {@code hashCode()} to break the cycle.
     * @return the list of servers in this mass
     */
    private List<Server> getServers() {
        return services.stream().map(Service::getServer).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlanningMass that = (PlanningMass) o;
        return pinned == that.pinned &&
                Objects.equals(getServiceTypes(), that.getServiceTypes()) &&
                Objects.equals(getServers(), getServers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getServiceTypes(), getServers(), pinned);
    }
}
