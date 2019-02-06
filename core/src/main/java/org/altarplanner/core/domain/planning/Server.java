package org.altarplanner.core.domain.planning;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.request.DateOffRequest;
import org.altarplanner.core.domain.request.DateTimeOnRequest;
import org.altarplanner.core.domain.request.ServiceTypeOffRequest;
import org.altarplanner.core.domain.state.Config;
import org.altarplanner.core.xml.jaxb.util.LocalDateRangeXmlAdapter;
import org.altarplanner.core.xml.jaxb.util.LocalDateTimeWithoutSecondsXmlAdapter;
import org.threeten.extra.LocalDateRange;

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
public class Server extends AbstractPersistable implements Comparable<Server> {

  private String surname;
  private String forename;
  private int year = LocalDate.now().getYear();
  private List<LocalDateRange> absences = new ArrayList<>();
  private List<DayOfWeek> weeklyAbsences = new ArrayList<>();
  private List<ServiceType> inabilities = new ArrayList<>();
  private List<LocalDateTime> dateTimeOnWishes = new ArrayList<>();

  public Server() {
    this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
    this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
  }

  @XmlID
  @XmlAttribute
  public String getXmlID() {
    return surname + "_" + forename + "-" + year;
  }

  public void setXmlID(String xmlID) {}

  public String getDesc() {
    return surname + ", " + forename;
  }

  public boolean isAvailableFor(Service service) {
    LocalDate date = service.getMass().getDateTime().toLocalDate();
    return !inabilities.contains(service.getType())
        && year <= service.getType().getMaxYear()
        && !weeklyAbsences.contains(date.getDayOfWeek())
        && absences.parallelStream().noneMatch(absence -> absence.contains(date));
  }

  public Stream<DateOffRequest> getDateOffRequests(
      Set<LocalDate> relevantDates, Set<LocalDate> feastDays) {
    final Set<DayOfWeek> weeklyAbsenceSet = Set.copyOf(weeklyAbsences);
    return relevantDates
        .parallelStream()
        .filter(
            date ->
                weeklyAbsenceSet.contains(date.getDayOfWeek()) && !feastDays.contains(date)
                    || absences.parallelStream().anyMatch(dateRange -> dateRange.contains(date)))
        .filter(
            date ->
                dateTimeOnWishes
                    .parallelStream()
                    .noneMatch(dateTime -> date.equals(dateTime.toLocalDate())))
        .map(date -> new DateOffRequest(this, date));
  }

  public Stream<ServiceTypeOffRequest> getServiceTypeOffRequests() {
    return inabilities
        .parallelStream()
        .filter(serviceType -> serviceType.getMaxYear() >= year)
        .map(serviceType -> new ServiceTypeOffRequest(this, serviceType));
  }

  public Stream<DateTimeOnRequest> getDateTimeOnRequests(Set<LocalDateTime> relevantDateTimes) {
    return dateTimeOnWishes
        .parallelStream()
        .filter(relevantDateTimes::contains)
        .map(dateTime -> new DateTimeOnRequest(this, dateTime));
  }

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

  @XmlIDREF
  @XmlElementWrapper(name = "inabilities")
  @XmlElement(name = "inability")
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

  @Override
  public int compareTo(Server o) {
    return Objects.compare(
        this,
        o,
        Comparator.comparing(Server::getSurname)
            .thenComparing(Server::getForename)
            .thenComparing(Server::getYear));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Server server = (Server) o;
    return year == server.year
        && Objects.equals(surname, server.surname)
        && Objects.equals(forename, server.forename);
  }

  @Override
  public int hashCode() {
    return Objects.hash(year, surname, forename);
  }

  @Override
  public String toString() {
    return "Server{" + getXmlID() + "}";
  }
}
