package org.altarplanner.core.xml;

import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.ScheduleTemplate;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;

import java.io.IOException;
import java.nio.file.Files;

import static org.altarplanner.core.domain.util.BigDomainGenerator.REPRODUCIBLE_CONSTRUCTION_SOLVER_CONFIG_RESOURCE;
import static org.altarplanner.core.xml.JaxbIOTests.EXPECTED_CONFIG;
import static org.altarplanner.core.xml.JaxbIOTests.EXPECTED_SCHEDULE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class XmlInconsistencyTests {

    @Test
    void configScheduleTemplateInconsistencyTest() throws IOException, UnexpectedElementException, UnknownJAXBException {
        final var config = JaxbIO.unmarshal(EXPECTED_CONFIG, Config.class);
        config.remove(config.getServiceTypes().get(0));
        config.getServiceTypes().remove(config.getServiceTypes().get(0));

        final var scheduleTemplate = JaxbIO.unmarshal(EXPECTED_SCHEDULE_TEMPLATE, ScheduleTemplate.class);
        final var schedulePath = Files.createTempFile(null, null);
        JaxbIO.marshal(new Schedule(scheduleTemplate, config), schedulePath.toFile());
        final var schedule = Schedule.load(schedulePath.toFile());

        final var solver = SolverFactory
                .createFromXmlResource(REPRODUCIBLE_CONSTRUCTION_SOLVER_CONFIG_RESOURCE)
                .buildSolver();
        assertDoesNotThrow(() -> solver.solve(schedule), "An inconsistency between scheduleTemplate and config");
    }
}
