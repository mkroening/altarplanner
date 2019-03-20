package org.altarplanner.core.xml;

import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class StrictJAXB {

  public static void marshal(Object jaxbObject, Path output) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(jaxbObject.getClass());
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(jaxbObject, output.toFile());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static <T> T unmarshal(Path input, Class<T> type) throws UnmarshalException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(type);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setEventHandler(event -> false);
      return unmarshaller.unmarshal(new StreamSource(input.toFile()), type).getValue();
    } catch (UnmarshalException e) {
      throw e;
    } catch (JAXBException e) {
      e.printStackTrace();
      return null;
    }
  }
}
