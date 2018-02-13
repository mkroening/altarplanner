package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Server {

    @Getter @Setter private String forename;
    @Getter @Setter private String surname;
    @Getter @Setter private int year = LocalDate.now().getYear();
    @Getter @Setter private List<DateSpan> absences = new ArrayList<>();
    @Getter @Setter private List<DayOfWeek> weeklyAbsences = new ArrayList<>();

    public Server() {
        this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
        this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
    }

}
