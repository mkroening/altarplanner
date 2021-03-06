package org.altarplanner.core.benchmark;

import org.altarplanner.core.planning.domain.state.Schedule;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;

public class RunBenchmarks {

  private static final String BENCHMARK_RESOURCE_PATH = "org/altarplanner/core/planning/benchmark/";

  public static void main(String[] args) {}

  private static void runBenchmark(PlannerBenchmarkFactory benchmarkFactory) {
    Schedule schedule = new Schedule();
    benchmarkFactory.buildPlannerBenchmark(schedule).benchmark();
  }

  private static void aggregateBenchmarks() {
    PlannerBenchmarkFactory plannerBenchmarkFactory =
        PlannerBenchmarkFactory.createFromXmlResource(
            BENCHMARK_RESOURCE_PATH + "defaultBenchmarkConfig.xml");
    BenchmarkAggregatorFrame.createAndDisplay(plannerBenchmarkFactory);
  }

  private static void runDefaultBenchmark() {
    runBenchmark(
        PlannerBenchmarkFactory.createFromXmlResource(
            BENCHMARK_RESOURCE_PATH + "defaultBenchmarkConfig.xml"));
  }

  private static void runTabuBenchmarks() {
    runBenchmark(
        PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
            BENCHMARK_RESOURCE_PATH + "tabuBenchmarkTemplate.xml.ftl"));
  }

  private static void runSimulatedAnnealingBenchmarks() {
    runBenchmark(
        PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
            BENCHMARK_RESOURCE_PATH + "simulatedAnnealingBenchmarkTemplate.xml.ftl"));
  }

  private static void runMultithreadingBenchmark() {
    runBenchmark(
        PlannerBenchmarkFactory.createFromXmlResource(
            BENCHMARK_RESOURCE_PATH + "multithreadingBenchmarkConfig.xml"));
  }
}
