package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

public class PairRequest extends GenericRequest {

    private final Server pairedWith;

    public PairRequest(Server server, Server pairedWith) {
        super(server);
        this.pairedWith = pairedWith;
    }

    public Server getPairedWith() {
        return pairedWith;
    }

}
