package org.altarplanner.core.solver;

import java.util.Comparator;
import java.util.Objects;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.Service;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

public class ServiceDifficultyWeightFactory
    implements SelectionSorterWeightFactory<Schedule, Service> {

  @Override
  public ServiceDifficultyWeight createSorterWeight(Schedule schedule, Service selection) {
    int availableServers = schedule.getAvailableServerCountFor(selection);
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
      return Objects.compare(
          other,
          this,
          Comparator.comparingInt((ServiceDifficultyWeight weight) -> weight.availableServers)
              .thenComparingInt(
                  serviceDifficultyWeight -> serviceDifficultyWeight.service.getPlanningId()));
    }
  }
}
