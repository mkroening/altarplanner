package org.altarplanner.core.benchmark;

import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;

public class RunBenchmarks {

    private final static String BENCHMARK_RESOURCE_PATH = "org/altarplanner/core/benchmark/";

    public static void main(String[] args) {

    }

    private static void runBenchmark(PlannerBenchmarkFactory benchmarkFactory) {
        Schedule schedule = BigDomainGenerator.genSchedule();
        benchmarkFactory.buildPlannerBenchmark(schedule).benchmark();
    }

    private static void aggregateBenchmarks() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(BENCHMARK_RESOURCE_PATH + "defaultBenchmarkConfig.xml");
        BenchmarkAggregatorFrame.createAndDisplay(plannerBenchmarkFactory);
    }

    private static void runDefaultBenchmark() {
        runBenchmark(PlannerBenchmarkFactory.createFromXmlResource(BENCHMARK_RESOURCE_PATH + "defaultBenchmarkConfig.xml"));
    }

    private static void runTabuBenchmarks() {
        runBenchmark(PlannerBenchmarkFactory.createFromFreemarkerXmlResource(BENCHMARK_RESOURCE_PATH + "tabuBenchmarkTemplate.xml.ftl"));
    }

    private static void runSimulatedAnnealingBenchmarks() {
        runBenchmark(PlannerBenchmarkFactory.createFromFreemarkerXmlResource(BENCHMARK_RESOURCE_PATH + "simulatedAnnealingBenchmarkTemplate.xml.ftl"));
    }

    private static void runMultithreadingBenchmark() {
        runBenchmark(PlannerBenchmarkFactory.createFromXmlResource(BENCHMARK_RESOURCE_PATH + "multithreadingBenchmarkConfig.xml"));
    }

}
