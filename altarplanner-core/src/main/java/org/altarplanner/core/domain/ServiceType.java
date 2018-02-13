package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;

public class ServiceType {

    @Getter @Setter private String name;
    @Getter @Setter int minExp = 0;
    @Getter @Setter int maxExp = 6;

    public ServiceType() {
        this.name = Config.RESOURCE_BUNDLE.getString("serviceType.name");
    }

}
