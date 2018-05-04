package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.ServiceType;

import java.util.AbstractMap;

public class ServiceTypeOffRequest extends AbstractMap.SimpleImmutableEntry<Server, ServiceType> {
    public ServiceTypeOffRequest(Server key, ServiceType value) {
        super(key, value);
    }
}
