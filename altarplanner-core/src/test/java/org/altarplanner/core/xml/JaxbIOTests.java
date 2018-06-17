package org.altarplanner.core.xml;

import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.util.BigDomainGenerator;
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

    private static final File EXPECTED_CONFIG = new File("src/test/resources/org/altarplanner/core/xml/bigDomainConfig.xml");

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
        assertEquals(expectedLines, marshalledLines);
    }

}
