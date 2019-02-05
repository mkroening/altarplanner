package org.altarplanner.core.solver;

import java.util.Comparator;
import java.util.Objects;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.planning.Server;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

public class ServerStrengthWeightFactory implements SelectionSorterWeightFactory<Schedule, Server> {

  @Override
  public ServerStrengthWeight createSorterWeight(Schedule schedule, Server selection) {
    int possibleServicesCount = schedule.getAvailableServiceCountFor(selection);
    return new ServerStrengthWeight(selection, possibleServicesCount);
  }

  public static class ServerStrengthWeight implements Comparable<ServerStrengthWeight> {

    private final Server server;
    private final int possibleServicesCount;

    ServerStrengthWeight(Server server, int possibleServicesCount) {
      this.server = server;
      this.possibleServicesCount = possibleServicesCount;
    }

    @Override
    public int compareTo(ServerStrengthWeight o) {
      return Objects.compare(
          o,
          this,
          Comparator.comparingInt((ServerStrengthWeight weight) -> weight.possibleServicesCount)
              .thenComparing(weight -> weight.server.getPlanningId()));
    }
  }
}
