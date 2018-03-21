package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.io.Serializable;

abstract class GenericRequest implements Serializable {

    private final Server server;

    GenericRequest(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

}
