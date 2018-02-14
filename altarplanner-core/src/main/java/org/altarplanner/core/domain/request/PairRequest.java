package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;

public class PairRequest extends GenericRequest {

    @Getter private final Server pairedWith;

    public PairRequest(Server server, Server pairedWith) {
        super(server);
        this.pairedWith = pairedWith;
    }

}
