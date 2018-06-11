package org.altarplanner.core.domain;

import com.migesok.jaxb.adapter.javatime.LocalDateTimeXmlAdapter;
import org.altarplanner.core.domain.request.*;
import org.altarplanner.core.xml.jaxb.util.DateSpanXmlAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@XmlType(propOrder = {"surname", "forename", "year", "id", "weeklyAbsences", "inabilities", "absences", "dateTimeOnWishes"})
public class Server implements Serializable {

    private String surname;
    private String forename;
    private int year = LocalDate.now().getYear();
    private List<LocalDateInterval> absences = new ArrayList<>();
    private List<DayOfWeek> weeklyAbsences = new ArrayList<>();
    private List<ServiceType> inabilities = new ArrayList<>();
    private List<LocalDateTime> dateTimeOnWishes = new ArrayList<>();

    public Server() {
        this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
        this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
    }

    @XmlID
    @XmlAttribute
    public String getId() {
        return surname + "_" + forename + "-" + year;
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

    Stream<DateOffRequest> getDateOffRequestParallelStream() {
        return absences.parallelStream()
                .flatMap(LocalDateInterval::stream)
                .map(date -> new DateOffRequest(this, date));
    }

    Stream<DayOffRequest> getDayOffRequestParallelStream() {
        return weeklyAbsences.parallelStream()
                .map(day -> new DayOffRequest(this, day));
    }

    Stream<ServiceTypeOffRequest> getServiceTypeOffRequestParallelStream() {
        return inabilities.parallelStream()
                .map(serviceType -> new ServiceTypeOffRequest(this, serviceType));
    }

    Stream<DateTimeOnRequest> getDateTimeOnRequestParallelStream() {
        return dateTimeOnWishes.parallelStream()
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

    @XmlElementWrapper(name = "absences")
    @XmlElement(name = "absence")
    @XmlJavaTypeAdapter(DateSpanXmlAdapter.class)
    public List<LocalDateInterval> getAbsences() {
        return absences;
    }

    public void setAbsences(List<LocalDateInterval> absences) {
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

    @XmlElementWrapper(name = "dateTimeOnWishes")
    @XmlElement(name = "dateTime")
    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
    public List<LocalDateTime> getDateTimeOnWishes() {
        return dateTimeOnWishes;
    }

    public void setDateTimeOnWishes(List<LocalDateTime> dateTimeOnWishes) {
        this.dateTimeOnWishes = dateTimeOnWishes;
    }

}
