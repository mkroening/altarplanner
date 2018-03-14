package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
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

    @Getter @Setter private String surname;
    @Getter @Setter private String forename;
    @Getter @Setter private int year = LocalDate.now().getYear();
    @Getter @Setter private List<DateSpan> absences = new ArrayList<>();
    @Getter @Setter private List<DayOfWeek> weeklyAbsences = new ArrayList<>();
    @Getter @Setter private List<ServiceType> inabilities = new ArrayList<>();
    @Getter @Setter private List<LocalDateTime> dateTimeOnWishes = new ArrayList<>();
    @Getter @Setter private List<Server> pairedWith = new ArrayList<>();

    public Server() {
        this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
        this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
    }

    public Server(Server server) {
        this.surname = server.surname;
        this.forename = server.forename;
        this.year = server.year;
        Optional.ofNullable(server.absences).ifPresent(absences -> this.absences.addAll(absences));
        Optional.ofNullable(server.weeklyAbsences).ifPresent(weeklyAbsences -> this.weeklyAbsences.addAll(weeklyAbsences));
        Optional.ofNullable(server.inabilities).ifPresent(inabilities -> this.inabilities.addAll(inabilities));
        Optional.ofNullable(server.dateTimeOnWishes).ifPresent(dateTimeOnWishes -> this.dateTimeOnWishes.addAll(dateTimeOnWishes));
        Optional.ofNullable(server.pairedWith).ifPresent(pairedWith -> this.pairedWith.addAll(pairedWith));
    }

    public String getDesc() {
        return surname + ", " + forename;
    }

    public static Comparator<Server> getNaturalOrderComparator() {
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

}
