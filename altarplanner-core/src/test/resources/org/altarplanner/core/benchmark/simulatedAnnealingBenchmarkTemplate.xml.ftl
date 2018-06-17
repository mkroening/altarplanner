<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
    <benchmarkDirectory>benchmarks</benchmarkDirectory>

    <inheritedSolverBenchmark>
        <solver>
            <solutionClass>org.altarplanner.core.domain.Schedule</solutionClass>
            <entityClass>org.altarplanner.core.domain.Service</entityClass>
            <scoreDirectorFactory>
                <scoreDrl>org/altarplanner/core/solver/scoreRules.drl</scoreDrl>
            </scoreDirectorFactory>
            <constructionHeuristic/>
            <termination>
                <minutesSpentLimit>5</minutesSpentLimit>
            </termination>
        </solver>
    </inheritedSolverBenchmark>

    <#list ["1hard/10soft", "1hard/20soft", "1hard/50soft", "1hard/70soft"] as startingTemperature>
      <solverBenchmark>
          <name>Simulated Annealing startingTemperature ${startingTemperature?replace("/", "_")}</name>
          <solver>
              <localSearch>
                  <acceptor>
                      <simulatedAnnealingStartingTemperature>${startingTemperature}</simulatedAnnealingStartingTemperature>
                  </acceptor>
              </localSearch>
          </solver>
      </solverBenchmark>
    </#list>
</plannerBenchmark>