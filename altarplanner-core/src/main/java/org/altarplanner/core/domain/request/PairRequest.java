package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class PairRequest extends GenericRequest {

    private final Server pairedWith;

    public PairRequest(Server server, Server pairedWith) {
        super(server);
        this.pairedWith = pairedWith;
    }

    public Server getPairedWith() {
        return pairedWith;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this )
            return true;
        if (obj.getClass() != getClass())
            return false;
        PairRequest rhs = (PairRequest) obj;
        return new EqualsBuilder()
                .append(getServer(), rhs.getServer())
                .append(pairedWith, rhs.pairedWith)
                .isEquals();
    }
}
