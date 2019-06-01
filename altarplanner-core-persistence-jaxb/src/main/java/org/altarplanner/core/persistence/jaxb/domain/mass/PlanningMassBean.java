package org.altarplanner.core.persistence.jaxb.domain.mass;

import org.altarplanner.core.persistence.jaxb.domain.planning.ServiceXmlAdapter;
import org.altarplanner.core.persistence.jaxb.util.LocalDateTimeWithoutSecondsXmlAdapter;
import org.altarplanner.core.planning.domain.planning.Service;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@XmlType(propOrder = {"dateTime", "church", "form", "annotation", "services"})
public class PlanningMassBean implements Serializable {
  private LocalDateTime dateTime;

  private String church;

  private String form;

  private String annotation;

  private List<Service> services;

  @XmlJavaTypeAdapter(LocalDateTimeWithoutSecondsXmlAdapter.class)
  @XmlAttribute
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  @XmlAttribute
  public String getChurch() {
    return church;
  }

  public void setChurch(String church) {
    this.church = church;
  }

  @XmlAttribute
  public String getForm() {
    return form;
  }

  public void setForm(String form) {
    this.form = form;
  }

  @XmlAttribute
  public String getAnnotation() {
    return annotation;
  }

  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }

  @XmlElementWrapper(name = "services")
  @XmlElement(name = "service")
  @XmlJavaTypeAdapter(ServiceXmlAdapter.class)
  public List<Service> getServices() {
    return services;
  }

  public void setServices(List<Service> services) {
    this.services = services;
  }
}
