package org.altarplanner.core.benchmark;

import org.altarplanner.core.persistence.jaxb.JAXB;
import org.altarplanner.core.planning.domain.state.Schedule;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Launcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

  public static void main(String[] args) {
    final var plannerBenchmarkFactory =
        PlannerBenchmarkFactory.createFromXmlInputStream(
            Launcher.class.getResourceAsStream("defaultBenchmarkConfig.xml"));
    final var schedules =
        Arrays.stream(args)
            .map(
                s -> {
                  try {
                    return Optional.of(JAXB.unmarshalSchedule(Path.of(s)));
                  } catch (JAXBException e) {
                    LOGGER.warn("Unable to unmarshal " + s);
                    return Optional.<Schedule>empty();
                  }
                })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toUnmodifiableList());
    final var plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark(schedules);
    plannerBenchmark.benchmarkAndShowReportInBrowser();
  }
}
