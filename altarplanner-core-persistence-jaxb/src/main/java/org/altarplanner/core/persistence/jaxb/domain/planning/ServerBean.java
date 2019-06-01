package org.altarplanner.core.persistence.jaxb.domain.planning;

import org.altarplanner.core.persistence.jaxb.util.LocalDateRangeXmlAdapter;
import org.altarplanner.core.persistence.jaxb.util.LocalDateTimeWithoutSecondsXmlAdapter;
import org.altarplanner.core.persistence.jaxb.util.ServiceTypeXmlIDAdapter;
import org.altarplanner.core.planning.domain.ServiceType;
import org.threeten.extra.LocalDateRange;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlType(
    propOrder = {
      "surname",
      "forename",
      "year",
      "xmlID",
      "absences",
      "dateTimeOnWishes",
      "weeklyAbsences",
      "inabilities"
    })
public class ServerBean implements Serializable {
  private String surname;

  private String forename;

  private int year;

  private String xmlID;

  private List<LocalDateRange> absences = new ArrayList<>();

  private List<DayOfWeek> weeklyAbsences = new ArrayList<>();

  private List<ServiceType> inabilities = new ArrayList<>();

  private List<LocalDateTime> dateTimeOnWishes = new ArrayList<>();

  @XmlAttribute
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @XmlAttribute
  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  @XmlID
  @XmlAttribute
  public String getXmlID() {
    return xmlID;
  }

  public void setXmlID(String xmlID) {
    this.xmlID = xmlID;
  }

  @XmlAttribute
  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @XmlList
  @XmlJavaTypeAdapter(LocalDateRangeXmlAdapter.class)
  public List<LocalDateRange> getAbsences() {
    return absences;
  }

  public void setAbsences(List<LocalDateRange> absences) {
    this.absences = absences;
  }

  @XmlList
  public List<DayOfWeek> getWeeklyAbsences() {
    return weeklyAbsences;
  }

  public void setWeeklyAbsences(List<DayOfWeek> weeklyAbsences) {
    this.weeklyAbsences = weeklyAbsences;
  }

  @XmlElementWrapper(name = "inabilities")
  @XmlElement(name = "inability")
  @XmlJavaTypeAdapter(ServiceTypeXmlIDAdapter.class)
  public List<ServiceType> getInabilities() {
    return inabilities;
  }

  public void setInabilities(List<ServiceType> inabilities) {
    this.inabilities = inabilities;
  }

  @XmlList
  @XmlJavaTypeAdapter(LocalDateTimeWithoutSecondsXmlAdapter.class)
  public List<LocalDateTime> getDateTimeOnWishes() {
    return dateTimeOnWishes;
  }

  public void setDateTimeOnWishes(List<LocalDateTime> dateTimeOnWishes) {
    this.dateTimeOnWishes = dateTimeOnWishes;
  }
}
