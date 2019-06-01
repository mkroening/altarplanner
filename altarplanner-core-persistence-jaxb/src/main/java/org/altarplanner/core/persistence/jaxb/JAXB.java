package org.altarplanner.core.persistence.jaxb;

import org.altarplanner.core.persistence.jaxb.domain.state.ConfigBean;
import org.altarplanner.core.persistence.jaxb.domain.state.ConfigXmlAdapter;
import org.altarplanner.core.persistence.jaxb.domain.state.ScheduleTemplateBean;
import org.altarplanner.core.persistence.jaxb.domain.state.ScheduleTemplateXmlAdapter;
import org.altarplanner.core.planning.domain.state.Config;
import org.altarplanner.core.planning.domain.state.ScheduleTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import java.nio.file.Path;

public class JAXB {

  private static void marshal(Object jaxbObject, Path output) throws JAXBException {
    final var jaxbContext = JAXBContext.newInstance(jaxbObject.getClass());
    final var marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.marshal(jaxbObject, output.toFile());
  }

  private static <T> T unmarshal(Path input, Class<T> type) throws JAXBException {
    final var jaxbContext = JAXBContext.newInstance(type);
    final var unmarshaller = jaxbContext.createUnmarshaller();
    unmarshaller.setEventHandler(event -> false);
    return unmarshaller.unmarshal(new StreamSource(input.toFile()), type).getValue();
  }

  public static void marshalConfig(Config config, Path output) throws JAXBException {
    final var configBean = new ConfigXmlAdapter().marshal(config);
    marshal(configBean, output);
  }

  public static Config unmarshalConfig(Path input) throws JAXBException {
    final var configBean = unmarshal(input, ConfigBean.class);
    return new ConfigXmlAdapter().unmarshal(configBean);
  }

  public static void marshalScheduleTemplate(ScheduleTemplate scheduleTemplate, Path output) throws JAXBException {
    final var scheduleTemplateBean = new ScheduleTemplateXmlAdapter().marshal(scheduleTemplate);
    marshal(scheduleTemplateBean, output);
  }

  public static ScheduleTemplate unmarshalScheduleTemplate(Path input) throws JAXBException {
    final var scheduleTemplateBean = unmarshal(input, ScheduleTemplateBean.class);
    return new ScheduleTemplateXmlAdapter().unmarshal(scheduleTemplateBean);
  }
}
