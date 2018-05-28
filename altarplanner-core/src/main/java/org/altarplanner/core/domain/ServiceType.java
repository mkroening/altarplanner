package org.altarplanner.core.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import java.io.Serializable;
import java.time.Year;
import java.util.Comparator;

public class ServiceType implements Serializable {

    private String name;
    private int maxYear = Year.now().getValue();
    private int minYear = Year.now().getValue() - 5;

    public ServiceType() {
        this.name = Config.RESOURCE_BUNDLE.getString("serviceType.name");
    }

    @XmlAttribute
    @XmlID
    public String getXmlID() {
        return name + "{" + maxYear + "-" + minYear + "}";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public int getMinYear() {
        return minYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

}
