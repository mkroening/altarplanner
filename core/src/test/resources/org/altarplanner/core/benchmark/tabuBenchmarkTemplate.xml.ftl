<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
    <benchmarkDirectory>benchmarks</benchmarkDirectory>

    <inheritedSolverBenchmark>
        <solver>
            <solutionClass>org.altarplanner.core.domain.state.Schedule</solutionClass>
            <entityClass>org.altarplanner.core.domain.planning.Service</entityClass>
            <scoreDirectorFactory>
                <scoreDrl>org/altarplanner/core/solver/scoreRules.drl</scoreDrl>
            </scoreDirectorFactory>
            <constructionHeuristic/>
            <termination>
                <minutesSpentLimit>5</minutesSpentLimit>
            </termination>
        </solver>
    </inheritedSolverBenchmark>

    <#list [5, 7, 11, 13] as entityTabuSize>
        <#list [500, 1000, 2000] as acceptedCountLimit>
            <solverBenchmark>
                <name>Tabu Search entityTabuSize ${entityTabuSize} acceptedCountLimit ${acceptedCountLimit}</name>
                <solver>
                    <localSearch>
                        <unionMoveSelector>
                            <changeMoveSelector/>
                            <swapMoveSelector/>
                        </unionMoveSelector>
                        <acceptor>
                            <entityTabuSize>${entityTabuSize}</entityTabuSize>
                        </acceptor>
                        <forager>
                            <acceptedCountLimit>${acceptedCountLimit}</acceptedCountLimit>
                        </forager>
                    </localSearch>
                </solver>
            </solverBenchmark>
        </#list>
    </#list>
</plannerBenchmark>