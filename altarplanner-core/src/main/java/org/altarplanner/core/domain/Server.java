package org.altarplanner.core.domain;

import org.altarplanner.core.domain.request.*;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Server implements Serializable {

    private String surname;
    private String forename;
    private int year = LocalDate.now().getYear();
    private List<DateSpan> absences = new ArrayList<>();
    private List<DayOfWeek> weeklyAbsences = new ArrayList<>();
    private List<ServiceType> inabilities = new ArrayList<>();
    private List<LocalDateTime> dateTimeOnWishes = new ArrayList<>();
    private List<Server> pairedWith = new ArrayList<>();

    public Server() {
        this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
        this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
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

    public void addAllPairedWith(List<Server> servers) {
        servers.parallelStream().forEach(server -> server.pairedWith.add(this));
        this.pairedWith.addAll(servers);
    }

    public void removeAllPairedWith(List<Server> servers) {
        servers.parallelStream().forEach(server -> server.pairedWith.remove(this));
        this.pairedWith.removeAll(servers);
    }

    public void removeFromAllPairs() {
        pairedWith.parallelStream().forEach(server -> server.pairedWith.remove(this));
        pairedWith.clear();
    }

    public boolean isAvailableFor(Service service) {
        LocalDate date = service.getMass().getDate();
        return !inabilities.contains(service.getType())
                && year <= service.getType().getMaxYear()
                && !weeklyAbsences.contains(date.getDayOfWeek())
                && absences.parallelStream().noneMatch(absence -> absence.contains(date));
    }

    public Stream<DateOffRequest> getDateOffRequestParallelStream() {
        return absences.parallelStream()
                .flatMap(DateSpan::getDateParallelStream)
                .map(date -> new DateOffRequest(this, date));
    }

    public Stream<DayOffRequest> getDayOffRequestParallelStream() {
        return weeklyAbsences.parallelStream()
                .map(day -> new DayOffRequest(this, day));
    }

    public Stream<ServiceTypeOffRequest> getServiceTypeOffRequestParallelStream() {
        return inabilities.parallelStream()
                .map(serviceType -> new ServiceTypeOffRequest(this, serviceType));
    }

    public Stream<DateTimeOnRequest> getDateTimeOnRequestParallelStream() {
        return dateTimeOnWishes.parallelStream()
                .map(dateTime -> new DateTimeOnRequest(this, dateTime));
    }

    public Stream<PairRequest> getPairRequestParallelStream() {
        return pairedWith.parallelStream()
                .map(pairedWith -> new PairRequest(this, pairedWith));
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<DateSpan> getAbsences() {
        return absences;
    }

    public void setAbsences(List<DateSpan> absences) {
        this.absences = absences;
    }

    public List<DayOfWeek> getWeeklyAbsences() {
        return weeklyAbsences;
    }

    public void setWeeklyAbsences(List<DayOfWeek> weeklyAbsences) {
        this.weeklyAbsences = weeklyAbsences;
    }

    public List<ServiceType> getInabilities() {
        return inabilities;
    }

    public void setInabilities(List<ServiceType> inabilities) {
        this.inabilities = inabilities;
    }

    public List<LocalDateTime> getDateTimeOnWishes() {
        return dateTimeOnWishes;
    }

    public void setDateTimeOnWishes(List<LocalDateTime> dateTimeOnWishes) {
        this.dateTimeOnWishes = dateTimeOnWishes;
    }

    public List<Server> getPairedWith() {
        return pairedWith;
    }

    public void setPairedWith(List<Server> pairedWith) {
        this.pairedWith = pairedWith;
    }

}
