package org.altarplanner.core.solver;

import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.Service;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

public class ServiceDifficultyWeightFactory implements SelectionSorterWeightFactory<Schedule, Service> {

    @Override
    public Comparable createSorterWeight(Schedule schedule, Service selection) {
        int availableServers = schedule.getAvailableServerCountAt(selection.getMass().getDate());
        return new ServiceDifficultyWeight(selection, availableServers);
    }

    public static class ServiceDifficultyWeight implements Comparable<ServiceDifficultyWeight> {

        private final Service service;
        private final int availableServers;

        ServiceDifficultyWeight(Service service, int availableServers) {
            this.service = service;
            this.availableServers = availableServers;
        }

        @Override
        public int compareTo(ServiceDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(other.availableServers, availableServers)
                    .append(other.service.getId(), service.getId())
                    .toComparison();
        }

    }

}
