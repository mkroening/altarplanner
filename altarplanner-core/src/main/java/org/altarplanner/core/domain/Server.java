package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class Server {

    @Getter @Setter private String forename;
    @Getter @Setter private String surname;
    @Getter @Setter private int year = LocalDate.now().getYear();

    public Server() {
        this.surname = Config.RESOURCE_BUNDLE.getString("server.surname");
        this.forename = Config.RESOURCE_BUNDLE.getString("server.forename");
    }

}
