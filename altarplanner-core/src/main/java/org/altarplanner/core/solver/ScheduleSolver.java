package org.altarplanner.core.solver;

import org.altarplanner.core.domain.Schedule;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
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

        // Log constraint break down
        try (ScoreDirector<Schedule> guiScoreDirector = solver.getScoreDirectorFactory().buildScoreDirector()) {
            guiScoreDirector.setWorkingSolution(bestSolution);
            LOGGER.debug("Constraint break down: score ({})", guiScoreDirector.calculateScore());

            guiScoreDirector.getConstraintMatchTotals().forEach(constraintMatchTotal -> {
                LOGGER.debug(
                        "Constraint: {} ({})",
                        constraintMatchTotal.getConstraintName(),
                        constraintMatchTotal.getScoreTotal());

                constraintMatchTotal.getConstraintMatchSet().forEach(constraintMatch ->
                        LOGGER.debug(
                                "Match ({}): {}",
                                constraintMatch.getConstraintName(),
                                constraintMatch.getJustificationList())
                );
            });
        }

        return bestSolution;
    }

}
