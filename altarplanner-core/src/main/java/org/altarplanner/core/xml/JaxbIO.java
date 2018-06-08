package org.altarplanner.core.xml;

import javax.xml.bind.*;
import java.io.File;

public class JaxbIO {

    public static void marshal(Object object, File output) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(object, output);
    }

    public static <T> T unmarshal(File input, Class<T> instanceOf) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(instanceOf);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return instanceOf.cast(unmarshaller.unmarshal(input));
    }

}
