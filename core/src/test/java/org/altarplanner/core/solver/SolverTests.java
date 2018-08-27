package org.altarplanner.core.solver;

import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;

class SolverTests {

    @Test
    void solverTest() {
        SolverFactory.createFromXmlResource("org/altarplanner/core/solver/fastAssertSolverConfig.xml")
                .buildSolver()
                .solve(BigDomainGenerator.genSchedule());
    }

}
