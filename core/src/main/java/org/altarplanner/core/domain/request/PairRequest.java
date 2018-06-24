package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;

import java.util.AbstractMap;

public class PairRequest extends AbstractMap.SimpleImmutableEntry<Server, Server> {
    public PairRequest(Server key, Server value) {
        super(key, value);
    }
}
