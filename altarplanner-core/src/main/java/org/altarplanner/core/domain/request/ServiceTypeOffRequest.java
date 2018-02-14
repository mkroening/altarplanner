package org.altarplanner.core.domain.request;

import lombok.Getter;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.ServiceType;

public class ServiceTypeOffRequest extends GenericRequest {

    @Getter private final ServiceType serviceType;

    public ServiceTypeOffRequest(Server server, ServiceType serviceType) {
        super(server);
        this.serviceType = serviceType;
    }

}
