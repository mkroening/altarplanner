package org.altarplanner.core.solver;

import org.altarplanner.core.domain.Schedule;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ScheduleSolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleSolver.class);
    private final Solver<Schedule> solver;

    public void addNewBestUiScoreStringConsumer(Consumer<String> consumer) {
        this.solver.addEventListener(event -> consumer.accept(event.getNewBestScore().toShortString()));
    }

    public void addNewBestScheduleConsumer(Consumer<Schedule> consumer) {
        this.solver.addEventListener(event -> consumer.accept(event.getNewBestSolution()));
    }

    public ScheduleSolver() {
        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource("org/altarplanner/core/solver/solverConfig.xml");
        this.solver = solverFactory.buildSolver();

        this.solver.addEventListener(bestSolutionChangedEvent ->
                LOGGER.info(
                        "New best score ({})",
                        bestSolutionChangedEvent.getNewBestScore()));
    }

    public Schedule solve(Schedule schedule) {
        Schedule bestSolution = solver.solve(schedule);
        LOGGER.debug(solver.explainBestScore());
        return bestSolution;
    }

    public boolean terminateEarly() {
        return solver.terminateEarly();
    }

}
