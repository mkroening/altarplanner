package org.altarplanner.core.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import java.io.File;
import java.io.FileNotFoundException;

public class JaxbIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbIO.class);

    public static void marshal(Object object, File output) throws UnknownJAXBException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, output);
        } catch (JAXBException e) {
            throw new UnknownJAXBException(e);
        }
    }

    public static <T> T unmarshal(File input, Class<T> instanceOf) throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        if (!input.exists())
            throw new FileNotFoundException(input + " (No such file or directory)");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(instanceOf);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return instanceOf.cast(unmarshaller.unmarshal(input));
        } catch (UnmarshalException e) {
            if (e.getMessage().startsWith("unexpected element")) {
                LOGGER.error("File corrupt: \"{}\".", input);
                throw new UnexpectedElementException(e);
            } else throw new UnknownJAXBException(e);
        } catch (JAXBException e) {
            throw new UnknownJAXBException(e);
        }
    }

}
