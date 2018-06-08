package org.altarplanner.core.domain.mass;

import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

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
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
