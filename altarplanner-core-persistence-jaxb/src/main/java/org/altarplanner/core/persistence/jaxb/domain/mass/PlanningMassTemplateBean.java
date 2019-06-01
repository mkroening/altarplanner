package org.altarplanner.core.persistence.jaxb.domain.mass;

import org.altarplanner.core.persistence.jaxb.util.LocalDateTimeWithoutSecondsXmlAdapter;
import org.altarplanner.core.persistence.jaxb.util.ServiceTypeCountsXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@XmlType(propOrder = {"dateTime", "church", "form", "annotation", "serviceTypeCounts"})
public class PlanningMassTemplateBean implements Serializable {
  private LocalDateTime dateTime;

  private String church;

  private String form;

  private String annotation;

  private Map<ServiceType, Integer> serviceTypeCounts;

  @XmlAttribute
  @XmlJavaTypeAdapter(LocalDateTimeWithoutSecondsXmlAdapter.class)
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

  @XmlJavaTypeAdapter(ServiceTypeCountsXmlAdapter.class)
  public Map<ServiceType, Integer> getServiceTypeCounts() {
    return serviceTypeCounts;
  }

  public void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts) {
    this.serviceTypeCounts = serviceTypeCounts;
  }
}
