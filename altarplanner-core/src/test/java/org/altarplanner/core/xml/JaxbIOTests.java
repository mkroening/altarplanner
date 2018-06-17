package org.altarplanner.core.xml;

import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.util.BigDomainGenerator;
import org.altarplanner.core.xml.jaxb.util.DiscreteMassCollection;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JaxbIOTests {

    private static final String XML_TEST_PATHNAME = "src/test/resources/org/altarplanner/core/xml/";
    private static final File EXPECTED_CONFIG = new File(XML_TEST_PATHNAME + "bigDomainConfig.xml");
    private static final File EXPECTED_DISCRETE_MASS_COLLECTION = new File(XML_TEST_PATHNAME + "bigDiscreteMassCollection.xml");

    @Test
    public void configUnmarshalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final Config expected = BigDomainGenerator.genConfig();
        final Config unmarshalled = JaxbIO.unmarshal(EXPECTED_CONFIG, Config.class);
        assertEquals(expected, unmarshalled);
    }

    @Test
    public void configMarshalling() throws IOException, UnknownJAXBException {
        final List<String> expectedLines = Files.lines(EXPECTED_CONFIG.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.genConfig(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

    @Test
    public void discreteMassCollectionUnmarschalling() throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        final DiscreteMassCollection expected = BigDomainGenerator.genMasses();
        final DiscreteMassCollection unmarshalled = JaxbIO.unmarshal(EXPECTED_DISCRETE_MASS_COLLECTION, DiscreteMassCollection.class);
        assertEquals(expected, unmarshalled);
    }

    @Test
    public void discreteMassCollectionMarshalling() throws IOException, UnknownJAXBException {
        final List<String> expectedLines = Files.lines(EXPECTED_DISCRETE_MASS_COLLECTION.toPath()).collect(Collectors.toUnmodifiableList());
        final Path marshalledPath = Files.createTempFile(null, null);
        JaxbIO.marshal(BigDomainGenerator.genMasses(), marshalledPath.toFile());
        final List<String> marshalledLines = Files.lines(marshalledPath).collect(Collectors.toUnmodifiableList());
        Files.delete(marshalledPath);
        assertEquals(expectedLines, marshalledLines);
    }

}
