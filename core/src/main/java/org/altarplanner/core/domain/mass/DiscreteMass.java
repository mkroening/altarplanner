package org.altarplanner.core.domain.mass;

import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Objects;

@XmlType(propOrder = {"date", "time", "church", "form", "serviceTypeCounts"})
public class DiscreteMass extends EditableMass {

    private LocalDate date;

    public DiscreteMass() {
        super();
        this.date = LocalDate.now().plusMonths(1);
    }

    public DiscreteMass(EditableMass editableMass, LocalDate date) {
        super(editableMass);
        this.date = date;
    }

    public String getDesc() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) + " - " +
                getTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " - " +
                getChurch();
    }

    public static Comparator<DiscreteMass> getDescComparator() {
        return Comparator
                .comparing(DiscreteMass::getDate)
                .thenComparing(GenericMass::getTime)
                .thenComparing(GenericMass::getChurch)
                .thenComparing(GenericMass::getForm);
    }

    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    @XmlAttribute
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DiscreteMass that = (DiscreteMass) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }

}
