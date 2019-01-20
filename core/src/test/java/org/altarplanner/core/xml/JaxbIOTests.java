package org.altarplanner.core.xml;

import org.altarplanner.core.domain.*;
import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.altarplanner.core.domain.ScheduleTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class JaxbIOTests {

    private static final String XML_TEST_PATHNAME = "src/test/resources/org/altarplanner/core/xml/";
    static final File EXPECTED_CONFIG = new File(XML_TEST_PATHNAME + "bigConfig.xml");
    static final File EXPECTED_SCHEDULE_TEMPLATE = new File(XML_TEST_PATHNAME + "bigScheduleTemplate.xml");
    private static final File EXPECTED_INITIALIZED_SCHEDULE = new File(XML_TEST_PATHNAME + "bigScheduleInitialized.xml");

    @Test
    @Disabled
    void writeExpectedFiles() throws UnknownJAXBException {
        JaxbIO.marshal(BigDomainGenerator.genConfig(), EXPECTED_CONFIG);
        JaxbIO.marshal(BigDomainGenerator.generateScheduleTemplate(), EXPECTED_SCHEDULE_TEMPLATE);
        JaxbIO.marshal(BigDomainGenerator.generateInitializedSchedule(), EXPECTED_INITIALIZED_SCHEDULE);
    }

    @Test
    void configUnmarshalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final Config expected = BigDomainGenerator.genConfig();
        final Config unmarshalled = JaxbIO.unmarshal(EXPECTED_CONFIG, Config.class);
        assertEquals(expected, unmarshalled);
    }

    @Test
    void configMarshalling() throws IOException, UnknownJAXBException {
        final List<String> expectedLines = Files.lines(EXPECTED_CONFIG.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.genConfig(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

    @Test
    void scheduleTemplateUnmarshalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final ScheduleTemplate expected = BigDomainGenerator.generateScheduleTemplate();
        final ScheduleTemplate unmarshalled = JaxbIO.unmarshal(EXPECTED_SCHEDULE_TEMPLATE, ScheduleTemplate.class);
        assertEquals(expected, unmarshalled);
    }

    @Test
    void scheduleTemplateMarshalling() throws IOException, UnknownJAXBException {
        final List<String> expectedLines = Files.lines(EXPECTED_SCHEDULE_TEMPLATE.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.generateScheduleTemplate(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

    @Test
    void scheduleUnmarshalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final Schedule expected = BigDomainGenerator.generateInitializedSchedule();
        final Schedule unmarshalled = Schedule.load(EXPECTED_INITIALIZED_SCHEDULE);
        assertEquals(expected, unmarshalled);
    }

    @Test
    void scheduleMarshalling() throws UnknownJAXBException, IOException {
        final List<String> expectedLines = Files.lines(EXPECTED_INITIALIZED_SCHEDULE.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.generateInitializedSchedule(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

    @Test
    void scheduleConstructionConfigIdentities() throws IOException, UnexpectedElementException, UnknownJAXBException {
        final Config config = JaxbIO.unmarshal(EXPECTED_CONFIG, Config.class);
        final Schedule oldSchedule = Schedule.load(EXPECTED_INITIALIZED_SCHEDULE);
        final PlanningMassTemplate newMass = new PlanningMassTemplate();
        newMass.setDateTime(LocalDateTime.of(oldSchedule.getPlanningWindow().getEnd(), LocalTime.of(11,0)));
        newMass.setServiceTypeCounts(Map.of(config.getServiceTypes().get(0), 1));
        final Schedule freshSchedule = new Schedule(new ScheduleTemplate(List.of(newMass)), oldSchedule, config);
        final int serviceIndex = 0;
        final int serverIndex = freshSchedule.getServers().indexOf(freshSchedule.getServices().get(0).getServer());

        assertEquals(freshSchedule.getServers().get(serverIndex), freshSchedule.getServices().get(serviceIndex).getServer());
        assertNotSame(freshSchedule.getServers().get(serverIndex), freshSchedule.getServices().get(serviceIndex).getServer());

        final Path tmpPath = Files.createTempFile(null, null);
        JaxbIO.marshal(freshSchedule, tmpPath.toFile());
        final Schedule unmarshalledSchedule = Schedule.load(tmpPath.toFile());
        Files.delete(tmpPath);

        assertEquals(unmarshalledSchedule.getServers().get(serverIndex), unmarshalledSchedule.getServices().get(serviceIndex).getServer());
        assertSame(unmarshalledSchedule.getServers().get(serverIndex), unmarshalledSchedule.getServices().get(serviceIndex).getServer());
    }

}
