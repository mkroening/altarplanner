package org.altarplanner.core.domain.mass;

import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.Service;
import org.altarplanner.core.domain.ServiceType;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DeepPlanningClone
@XmlType(propOrder = {"date", "time", "church", "form", "services"})
public class PlanningMass extends GenericMass {

    private List<Service> services;
    private LocalDate date;

    public PlanningMass() {
    }

    public PlanningMass(DiscreteMass discreteMass) {
        super(discreteMass);

        this.services = discreteMass.getServiceTypeCount().entrySet().parallelStream()
                .flatMap(serviceTypeCountEntry ->
                        IntStream.range(0, serviceTypeCountEntry.getValue())
                                .mapToObj(value -> new Service(this, serviceTypeCountEntry.getKey())))
                .sorted(Comparator.comparing(Service::getType, ServiceType.getDescComparator()))
                .collect(Collectors.toList());

        this.date = discreteMass.getDate();
    }

    public String getDateTimeString() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)) + " - " +
                getTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }

    public String getChurchFormString() {
        return getChurch() + " - " +
                getForm();
    }

    public String serviceDescOf(Server server) {
        Optional<Service> optionalService = services.parallelStream().filter(service -> service.getServer() == server).findAny();
        return optionalService.map(service -> service.getType().getDesc()).orElse(null);
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
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
        PlanningMass that = (PlanningMass) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }

}
