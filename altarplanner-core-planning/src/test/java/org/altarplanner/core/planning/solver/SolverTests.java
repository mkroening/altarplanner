package org.altarplanner.core.planning.solver;

import org.altarplanner.core.planning.domain.util.BigDomainGenerator;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;

public class SolverTests {

  public static final String FAST_ASSERT_SOLVER_CONFIG_RESOURCE =
      "org/altarplanner/core/planning/solver/fastAssertSolverConfig.xml";

  @Test
  void solverTest() {
    SolverFactory.createFromXmlResource(FAST_ASSERT_SOLVER_CONFIG_RESOURCE)
        .buildSolver()
        .solve(BigDomainGenerator.generateSchedule());
  }
}
