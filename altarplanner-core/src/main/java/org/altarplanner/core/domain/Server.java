package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.request.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Server {

    @Getter @Setter private String forename;
    @Getter @Setter private String surname;
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
