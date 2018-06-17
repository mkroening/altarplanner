package org.altarplanner.core.benchmark;

import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;

public class RunBenchmarks {

    private final static String BENCHMARK_RESOURCE_PATH = "org/altarplanner/core/benchmark/";

    public static void main(String[] args) {

    }

    private static void aggregateBenchmarks() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(BENCHMARK_RESOURCE_PATH + "defaultBenchmarkConfig.xml");
        BenchmarkAggregatorFrame.createAndDisplay(plannerBenchmarkFactory);
    }

    private static void runDefaultBenchmark() {
        Schedule schedule = BigDomainGenerator.genSchedule();
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(BENCHMARK_RESOURCE_PATH + "defaultBenchmarkConfig.xml");
        benchmarkFactory.buildPlannerBenchmark(schedule).benchmark();
    }

    private static void runTabuBenchmarks() {
        Schedule schedule = BigDomainGenerator.genSchedule();
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(BENCHMARK_RESOURCE_PATH + "tabuBenchmarkTemplate.xml.ftl");
        benchmarkFactory.buildPlannerBenchmark(schedule).benchmark();
    }

    private static void runSimulatedAnnealingBenchmarks() {
        Schedule schedule = BigDomainGenerator.genSchedule();
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(BENCHMARK_RESOURCE_PATH + "simulatedAnnealingBenchmarkTemplate.xml.ftl");
        benchmarkFactory.buildPlannerBenchmark(schedule).benchmark();
    }

}
