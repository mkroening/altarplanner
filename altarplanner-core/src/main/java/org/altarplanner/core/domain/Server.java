package org.altarplanner.core.domain;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.request.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Server {

    @Getter @Setter private String forename;
    @Getter @Setter private String surname;
    @Getter @Setter private int year = LocalDate.now().getYear();
    @XStreamImplicit @Getter @Setter private List<DateSpan> absences = new ArrayList<>();
    @XStreamImplicit @Getter @Setter private List<DayOfWeek> weeklyAbsences = new ArrayList<>();
    @XStreamImplicit @Getter @Setter private List<ServiceType> inabilities = new ArrayList<>();
    @XStreamImplicit @Getter @Setter private List<LocalDateTime> dateTimeOnWishes = new ArrayList<>();
    @XStreamImplicit @Getter @Setter private List<Server> pairedWith = new ArrayList<>();

    public Server() {
        this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
        this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
    }

    public String getDesc() {
        return surname + ", " + forename;
    }

    public static Comparator<Server> getAlphabeticComparator() {
        return Comparator
                .comparing(Server::getSurname)
                .thenComparing(Server::getForename)
                .thenComparing(Server::getYear);
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
