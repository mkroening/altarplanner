package org.altarplanner.core.domain.request;

import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.ServiceType;

public class ServiceTypeOffRequest extends GenericRequest {

    private final ServiceType serviceType;

    public ServiceTypeOffRequest(Server server, ServiceType serviceType) {
        super(server);
        this.serviceType = serviceType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

}
