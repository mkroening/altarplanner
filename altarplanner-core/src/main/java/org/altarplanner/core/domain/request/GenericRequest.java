package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

public abstract class GenericRequest {

    @Getter private final Server server;

    GenericRequest(Server server) {
        this.server = server;
    }

}
