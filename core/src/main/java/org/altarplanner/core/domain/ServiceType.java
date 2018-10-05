package org.altarplanner.core.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.time.Year;
import java.util.Comparator;
import java.util.Objects;

@XmlType(propOrder = {"name", "maxYear", "minYear", "xmlID"})
public class ServiceType implements Comparable<ServiceType>, Serializable {

    private String name;
    private int maxYear = Year.now().getValue();
    private int minYear = Year.now().getValue() - 5;

    public ServiceType() {
        this.name = Config.RESOURCE_BUNDLE.getString("serviceType.name");
    }

    @XmlAttribute
    @XmlID
    public String getXmlID() {
        return name +
                "_" + maxYear
                + "-" + minYear;
    }

    public String getDesc() {
        return name +
                " (" + Config.RESOURCE_BUNDLE.getString("serviceType.year") + ": " +
                maxYear + " - " + minYear + ")";
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public int getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    @XmlAttribute
    public int getMinYear() {
        return minYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    @Override
    public int compareTo(ServiceType o) {
        return Objects.compare(this, o, Comparator
                .comparing(ServiceType::getName)
                .thenComparing(Comparator.comparing(ServiceType::getMaxYear).reversed())
                .thenComparing(Comparator.comparing(ServiceType::getMinYear).reversed()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceType that = (ServiceType) o;
        return maxYear == that.maxYear &&
                minYear == that.minYear &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxYear, minYear);
    }

    @Override
    public String toString() {
        return "ServiceType{" +
                getXmlID() +
                "}";
    }

}
