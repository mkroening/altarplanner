package org.altarplanner.core.xml;

import org.altarplanner.core.domain.*;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.altarplanner.core.xml.jaxb.util.DiscreteMassCollection;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class JaxbIOTests {

    private static final String XML_TEST_PATHNAME = "src/test/resources/org/altarplanner/core/xml/";
    private static final File EXPECTED_CONFIG = new File(XML_TEST_PATHNAME + "bigDomainConfig.xml");
    private static final File EXPECTED_DISCRETE_MASS_COLLECTION = new File(XML_TEST_PATHNAME + "bigDiscreteMassCollection.xml");
    private static final File EXPECTED_INITIALIZED_SCHEDULE = new File(XML_TEST_PATHNAME + "bigInitializedSchedule.xml");

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
    void discreteMassCollectionUnmarshalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final DiscreteMassCollection expected = BigDomainGenerator.genMasses();
        final DiscreteMassCollection unmarshalled = JaxbIO.unmarshal(EXPECTED_DISCRETE_MASS_COLLECTION, DiscreteMassCollection.class);
        assertEquals(expected, unmarshalled);
    }

    @Test
    void discreteMassCollectionMarshalling() throws IOException, UnknownJAXBException {
        final List<String> expectedLines = Files.lines(EXPECTED_DISCRETE_MASS_COLLECTION.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.genMasses(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

    @Test
    void scheduleUnmarshalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final Schedule expected = BigDomainGenerator.genInitializedSchedule();
        final Schedule unmarshalled = Schedule.load(EXPECTED_INITIALIZED_SCHEDULE);
        assertEquals(expected, unmarshalled);
    }

    @Test
    void scheduleMarshalling() throws UnknownJAXBException, IOException {
        final List<String> expectedLines = Files.lines(EXPECTED_INITIALIZED_SCHEDULE.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.genInitializedSchedule(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

    @Test
    void scheduleConstructionConfigIdentities() throws IOException, UnexpectedElementException, UnknownJAXBException {
        final Config config = JaxbIO.unmarshal(EXPECTED_CONFIG, Config.class);
        final Schedule oldSchedule = Schedule.load(EXPECTED_INITIALIZED_SCHEDULE);

        final DiscreteMass newMass = new DiscreteMass();
        newMass.setDate(LocalDate.of(2018, 2 ,2));
        newMass.setServiceTypeCount(Map.of(config.getServiceTypes().get(1), 1));

        final Schedule tmpSchedule = new Schedule(config, List.of(newMass), oldSchedule);

        assertEquals(tmpSchedule.getServers().get(1), tmpSchedule.getPlanningMasses().get(10).getServices().get(3).getServer());
        assertNotSame(tmpSchedule.getServers().get(1), tmpSchedule.getPlanningMasses().get(10).getServices().get(3).getServer());

        final Path tmpPath = Files.createTempFile(null, null);
        JaxbIO.marshal(tmpSchedule, tmpPath.toFile());
        final Schedule newSchedule = Schedule.load(tmpPath.toFile());
        Files.delete(tmpPath);

        assertEquals(newSchedule.getServers().get(1), newSchedule.getPlanningMasses().get(10).getServices().get(3).getServer());
        assertSame(newSchedule.getServers().get(1), newSchedule.getPlanningMasses().get(10).getServices().get(3).getServer());
    }

}
