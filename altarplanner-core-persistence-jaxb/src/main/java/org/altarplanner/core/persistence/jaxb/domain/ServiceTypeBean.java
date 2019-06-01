package org.altarplanner.core.persistence.jaxb.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(propOrder = {"name", "maxYear", "minYear", "xmlID"})
public class ServiceTypeBean implements Serializable {
  private String name;

  private int maxYear;

  private int minYear;

  private String xmlID;

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlAttribute
  public int getMaxYear() {
    return maxYear;
  }

  public void setMaxYear(int maxYear) {
    this.maxYear = maxYear;
  }

  @XmlAttribute
  public int getMinYear() {
    return minYear;
  }

  public void setMinYear(int minYear) {
    this.minYear = minYear;
  }

  @XmlAttribute
  @XmlID
  public String getXmlID() {
    return xmlID;
  }

  public void setXmlID(String xmlID) {
    this.xmlID = xmlID;
  }
}
