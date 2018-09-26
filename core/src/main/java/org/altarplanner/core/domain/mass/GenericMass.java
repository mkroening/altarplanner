package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.Config;
import org.altarplanner.core.xml.jaxb.util.LocalTimeWithoutSecondsXmlAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Objects;

@XmlTransient
public abstract class GenericMass implements Serializable {

    private LocalTime time;
    private String church;
    private String form;

    GenericMass() {
        this.time = LocalTime.of(11, 0);
        this.church = Config.RESOURCE_BUNDLE.getString("mass.church");
        this.form = Config.RESOURCE_BUNDLE.getString("mass.form");
    }

    GenericMass(GenericMass genericMass) {
        this.time = genericMass.time;
        this.church = genericMass.church;
        this.form = genericMass.form;
    }

    public String getGenericDesc() {
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " - " +
                church;
    }

    public static Comparator<GenericMass> getGenericDescComparator() {
        return Comparator
                .comparing(GenericMass::getTime)
                .thenComparing(GenericMass::getChurch)
                .thenComparing(GenericMass::getForm);
    }

    @XmlJavaTypeAdapter(LocalTimeWithoutSecondsXmlAdapter.class)
    @XmlAttribute
    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @XmlAttribute
    public String getChurch() {
        return church;
    }

    public void setChurch(String church) {
        this.church = church;
    }

    @XmlAttribute
    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericMass that = (GenericMass) o;
        return Objects.equals(time, that.time) &&
                Objects.equals(church, that.church) &&
                Objects.equals(form, that.form);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, church, form);
    }

}
