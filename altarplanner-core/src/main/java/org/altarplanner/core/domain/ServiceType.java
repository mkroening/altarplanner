package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Year;

public class ServiceType {

    @Getter @Setter private String name;
    @Getter @Setter int maxYear = Year.now().getValue();
    @Getter @Setter int minYear = Year.now().getValue() - 6;

    public ServiceType() {
        this.name = Config.RESOURCE_BUNDLE.getString("serviceType.name");
    }

}
