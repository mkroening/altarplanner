package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

import java.io.Serializable;

abstract class GenericRequest implements Serializable {

    @Getter private final Server server;

    GenericRequest(Server server) {
        this.server = server;
    }

}
