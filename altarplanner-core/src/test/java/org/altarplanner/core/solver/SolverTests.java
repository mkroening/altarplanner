package org.altarplanner.core.solver;

import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

class SolverTests {

    @Test
    void solverTest() {
        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource("org/altarplanner/core/solver/solverConfig.xml");
        solverFactory.getSolverConfig().setEnvironmentMode(EnvironmentMode.FAST_ASSERT);
        solverFactory.getSolverConfig().setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(100000L));

        solverFactory.buildSolver().solve(BigDomainGenerator.genSchedule());
    }

}
