package org.altarplanner.core.domain;

import org.altarplanner.core.domain.request.*;
import org.altarplanner.core.xml.jaxb.util.LocalDateRangeXmlAdapter;
import org.altarplanner.core.xml.jaxb.util.LocalDateTimeWithoutSecondsXmlAdapter;
import org.threeten.extra.LocalDateRange;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@XmlType(propOrder = {"surname", "forename", "year", "xmlID", "weeklyAbsences", "inabilities", "absences", "dateTimeOnWishes"})
public class Server extends AbstractPersistable {

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
        return surname +
                "_" + forename +
                "-" + year;
    }

    public String getDesc() {
        return surname + ", " + forename;
    }

    public static Comparator<Server> getDescComparator() {
        return Comparator
                .comparing(Server::getSurname)
                .thenComparing(Server::getForename)
                .thenComparing(Server::getYear);
    }

    boolean isAvailableFor(Service service) {
        LocalDate date = service.getMass().getDate();
        return !inabilities.contains(service.getType())
                && year <= service.getType().getMaxYear()
                && !weeklyAbsences.contains(date.getDayOfWeek())
                && absences.parallelStream().noneMatch(absence -> absence.contains(date));
    }

    Stream<DateOffRequest> getDateOffRequests(Set<LocalDate> relevantDates) {
        final Set<DayOfWeek> weeklyAbsenceSet = Set.copyOf(weeklyAbsences);
        return relevantDates.parallelStream()
                .filter(date -> weeklyAbsenceSet.contains(date.getDayOfWeek()) || absences.parallelStream().anyMatch(interval -> interval.contains(date)))
                .filter(date -> dateTimeOnWishes.parallelStream().noneMatch(dateTime -> date.equals(dateTime.toLocalDate())))
                .map(date -> new DateOffRequest(this, date));
    }

    Stream<ServiceTypeOffRequest> getServiceTypeOffRequests() {
        return inabilities.parallelStream()
                .filter(serviceType -> serviceType.getMaxYear() >= year)
                .map(serviceType -> new ServiceTypeOffRequest(this, serviceType));
    }

    Stream<DateTimeOnRequest> getDateTimeOnRequests(Set<LocalDateTime> relevantDateTimes) {
        return dateTimeOnWishes.parallelStream()
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
    @XmlList
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return year == server.year &&
                Objects.equals(surname, server.surname) &&
                Objects.equals(forename, server.forename) &&
                Objects.equals(absences, server.absences) &&
                Objects.equals(weeklyAbsences, server.weeklyAbsences) &&
                Objects.equals(inabilities, server.inabilities) &&
                Objects.equals(dateTimeOnWishes, server.dateTimeOnWishes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surname, forename, year, absences, weeklyAbsences, inabilities, dateTimeOnWishes);
    }

    @Override
    public String toString() {
        return "Server{" +
                getXmlID() +
                "}";
    }

}
