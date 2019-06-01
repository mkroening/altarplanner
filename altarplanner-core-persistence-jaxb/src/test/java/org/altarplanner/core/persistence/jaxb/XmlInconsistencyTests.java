package org.altarplanner.core.persistence.jaxb;

import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.planning.domain.state.Schedule;
import org.altarplanner.core.planning.domain.state.ScheduleTemplate;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.altarplanner.core.persistence.jaxb.JAXBTests.EXPECTED_CONFIG;
import static org.altarplanner.core.persistence.jaxb.JAXBTests.EXPECTED_INITIALIZED_SCHEDULE;
import static org.altarplanner.core.persistence.jaxb.JAXBTests.EXPECTED_SCHEDULE_TEMPLATE;

class XmlInconsistencyTests {

  @Test
  void configScheduleTemplateInconsistencyTest() throws IOException, JAXBException {
    final var config = JAXB.unmarshalConfig(EXPECTED_CONFIG);

    config.remove(config.getServiceTypes().get(0));
    config.getServiceTypes().remove(config.getServiceTypes().get(0));

    final var newServiceType = new ServiceType();
    newServiceType.setName("New Service Type");
    config.getServiceTypes().add(newServiceType);
    config.getServers().get(0).getInabilities().add(newServiceType);

    final var scheduleTemplate = ScheduleTemplate.unmarshal(EXPECTED_SCHEDULE_TEMPLATE);
    final var schedule = new Schedule(scheduleTemplate, config);

    final var schedulePath = Files.createTempFile(null, null);
    schedule.marshal(schedulePath);
    Schedule.unmarshal(schedulePath);
  }

  @Test
  void configPastScheduleAllServersInconsistencyTest() throws IOException, JAXBException {
    final var config = JAXB.unmarshalConfig(EXPECTED_CONFIG);
    config.setPairs(List.of());
    config.setServers(List.of());

    final var pastSchedule = Schedule.unmarshal(EXPECTED_INITIALIZED_SCHEDULE);

    final var planningMassTemplate = new PlanningMassTemplate();
    planningMassTemplate.setDateTime(
        LocalDateTime.of(pastSchedule.getPlanningWindow().getEnd(), LocalTime.MIDNIGHT));
    planningMassTemplate.setServiceTypeCounts(Map.of(config.getServiceTypes().get(0), 1));
    final var scheduleTemplate = new ScheduleTemplate(List.of(planningMassTemplate));
    final var schedule = new Schedule(scheduleTemplate, pastSchedule, config);

    final var schedulePath = Files.createTempFile(null, null);
    schedule.marshal(schedulePath);
    Schedule.unmarshal(schedulePath);
  }

  @Test
  void configPastScheduleSomeServersInconsistencyTest() throws IOException, JAXBException {
    final var config = JAXB.unmarshalConfig(EXPECTED_CONFIG);
    config.setPairs(List.of());
    config.setServers(config.getServers().subList(0, config.getServers().size() / 2));

    final var pastSchedule = Schedule.unmarshal(EXPECTED_INITIALIZED_SCHEDULE);

    final var planningMassTemplate = new PlanningMassTemplate();
    planningMassTemplate.setDateTime(
        LocalDateTime.of(pastSchedule.getPlanningWindow().getEnd(), LocalTime.MIDNIGHT));
    planningMassTemplate.setServiceTypeCounts(Map.of(config.getServiceTypes().get(0), 1));
    final var scheduleTemplate = new ScheduleTemplate(List.of(planningMassTemplate));
    final var schedule = new Schedule(scheduleTemplate, pastSchedule, config);

    final var schedulePath = Files.createTempFile(null, null);
    schedule.marshal(schedulePath);
    Schedule.unmarshal(schedulePath);
  }
}
