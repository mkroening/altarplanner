package org.altarplanner.core.planning.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.UnmarshalException;
import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.planning.domain.state.Config;
import org.altarplanner.core.planning.domain.state.Schedule;
import org.altarplanner.core.planning.domain.state.ScheduleTemplate;
import org.altarplanner.core.planning.domain.util.BigDomainGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JaxbIOTests {

  private static final String XML_TEST_PATHNAME = "src/test/resources/org/altarplanner/core/planning/xml/";
  static final Path EXPECTED_CONFIG = Path.of(XML_TEST_PATHNAME + "bigConfig.xml");
  static final Path EXPECTED_SCHEDULE_TEMPLATE =
      Path.of(XML_TEST_PATHNAME + "bigScheduleTemplate.xml");
  static final Path EXPECTED_INITIALIZED_SCHEDULE =
      Path.of(XML_TEST_PATHNAME + "bigScheduleInitialized.xml");

  @Test
  @Disabled
  void writeExpectedFiles() {
    BigDomainGenerator.genConfig().marshal(EXPECTED_CONFIG);
    BigDomainGenerator.generateScheduleTemplate().marshal(EXPECTED_SCHEDULE_TEMPLATE);
    BigDomainGenerator.generateInitializedSchedule().marshal(EXPECTED_INITIALIZED_SCHEDULE);
  }

  @Test
  void configUnmarshalling() throws UnmarshalException {
    final Config expected = BigDomainGenerator.genConfig();
    final Config unmarshalled = Config.unmarshal(EXPECTED_CONFIG);
    assertEquals(expected, unmarshalled);
  }

  @Test
  void configMarshalling() throws IOException {
    final List<String> expectedLines =
        Files.lines(EXPECTED_CONFIG).collect(Collectors.toUnmodifiableList());
    final Path marshalledPath = Files.createTempFile(null, null);
    BigDomainGenerator.genConfig().marshal(marshalledPath);
    final List<String> marshalledLines =
        Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
    Files.delete(marshalledPath);
    assertEquals(expectedLines, marshalledLines);
  }

  @Test
  void scheduleTemplateUnmarshalling() throws UnmarshalException {
    final ScheduleTemplate expected = BigDomainGenerator.generateScheduleTemplate();
    final ScheduleTemplate unmarshalled = ScheduleTemplate.unmarshal(EXPECTED_SCHEDULE_TEMPLATE);
    assertEquals(expected, unmarshalled);
  }

  @Test
  void scheduleTemplateMarshalling() throws IOException {
    final List<String> expectedLines =
        Files.lines(EXPECTED_SCHEDULE_TEMPLATE).collect(Collectors.toUnmodifiableList());
    final Path marshalledPath = Files.createTempFile(null, null);
    BigDomainGenerator.generateScheduleTemplate().marshal(marshalledPath);
    final List<String> marshalledLines =
        Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
    Files.delete(marshalledPath);
    assertEquals(expectedLines, marshalledLines);
  }

  @Test
  void scheduleUnmarshalling() throws UnmarshalException {
    final Schedule expected = BigDomainGenerator.generateInitializedSchedule();
    final Schedule unmarshalled = Schedule.unmarshal(EXPECTED_INITIALIZED_SCHEDULE);
    assertEquals(expected, unmarshalled);
  }

  @Test
  void scheduleMarshalling() throws IOException {
    final List<String> expectedLines =
        Files.lines(EXPECTED_INITIALIZED_SCHEDULE).collect(Collectors.toUnmodifiableList());
    final Path marshalledPath = Files.createTempFile(null, null);
    BigDomainGenerator.generateInitializedSchedule().marshal(marshalledPath);
    final List<String> marshalledLines =
        Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
    Files.delete(marshalledPath);
    assertEquals(expectedLines, marshalledLines);
  }

  @Test
  void scheduleConstructionConfigIdentities() throws IOException, UnmarshalException {
    final Config config = Config.unmarshal(EXPECTED_CONFIG);
    final Schedule oldSchedule = Schedule.unmarshal(EXPECTED_INITIALIZED_SCHEDULE);
    final PlanningMassTemplate newMass = new PlanningMassTemplate();
    newMass.setDateTime(
        LocalDateTime.of(oldSchedule.getPlanningWindow().getEnd(), LocalTime.of(11, 0)));
    newMass.setServiceTypeCounts(Map.of(config.getServiceTypes().get(0), 1));
    final Schedule freshSchedule =
        new Schedule(new ScheduleTemplate(List.of(newMass)), oldSchedule, config);
    final int serviceIndex = 0;
    final int serverIndex =
        freshSchedule.getServers().indexOf(freshSchedule.getServices().get(0).getServer());

    assertEquals(
        freshSchedule.getServers().get(serverIndex),
        freshSchedule.getServices().get(serviceIndex).getServer());
    assertNotSame(
        freshSchedule.getServers().get(serverIndex),
        freshSchedule.getServices().get(serviceIndex).getServer());

    final Path tmpPath = Files.createTempFile(null, null);
    freshSchedule.marshal(tmpPath);
    final Schedule unmarshalledSchedule = Schedule.unmarshal(tmpPath);
    Files.delete(tmpPath);

    assertEquals(
        unmarshalledSchedule.getServers().get(serverIndex),
        unmarshalledSchedule.getServices().get(serviceIndex).getServer());
    assertSame(
        unmarshalledSchedule.getServers().get(serverIndex),
        unmarshalledSchedule.getServices().get(serviceIndex).getServer());
  }
}