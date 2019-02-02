package org.altarplanner.core.xml;

import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.ScheduleTemplate;
import org.altarplanner.core.domain.ServiceType;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;

import javax.xml.bind.UnmarshalException;
import java.io.IOException;
import java.nio.file.Files;

import static org.altarplanner.core.domain.util.BigDomainGenerator.REPRODUCIBLE_CONSTRUCTION_SOLVER_CONFIG_RESOURCE;
import static org.altarplanner.core.xml.JaxbIOTests.EXPECTED_CONFIG;
import static org.altarplanner.core.xml.JaxbIOTests.EXPECTED_SCHEDULE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class XmlInconsistencyTests {

    @Test
    void configScheduleTemplateInconsistencyTest() throws IOException, UnmarshalException {
        final var config = Config.unmarshal(EXPECTED_CONFIG);

        config.remove(config.getServiceTypes().get(0));
        config.getServiceTypes().remove(config.getServiceTypes().get(0));

        final var newServiceType = new ServiceType();
        newServiceType.setName("New Service Type");
        config.getServiceTypes().add(newServiceType);
        config.getServers().get(0).getInabilities().add(newServiceType);

        final var scheduleTemplate = ScheduleTemplate.unmarshal(EXPECTED_SCHEDULE_TEMPLATE);
        final var schedulePath = Files.createTempFile(null, null);
        new Schedule(scheduleTemplate, config).marshal(schedulePath);
        final var schedule = Schedule.unmarshal(schedulePath);

        final var solver = SolverFactory
                .createFromXmlResource(REPRODUCIBLE_CONSTRUCTION_SOLVER_CONFIG_RESOURCE)
                .buildSolver();
        assertDoesNotThrow(() -> solver.solve(schedule), "An inconsistency between scheduleTemplate and config");
    }
}
