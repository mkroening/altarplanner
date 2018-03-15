package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Year;
import java.util.Comparator;

public class ServiceType implements Serializable {

    @Getter @Setter private String name;
    @Getter @Setter private int maxYear = Year.now().getValue();
    @Getter @Setter private int minYear = Year.now().getValue() - 5;

    public ServiceType() {
        this.name = Config.RESOURCE_BUNDLE.getString("serviceType.name");
    }

    public String getDesc() {
        return name +
                " (" + Config.RESOURCE_BUNDLE.getString("serviceType.year") + ": " +
                maxYear + " - " + minYear + ")";
    }

    public static Comparator<ServiceType> getDescComparator() {
        return Comparator
                .comparing(ServiceType::getName)
                .thenComparing(ServiceType::getMaxYear)
                .thenComparing(ServiceType::getMinYear);
    }

}
