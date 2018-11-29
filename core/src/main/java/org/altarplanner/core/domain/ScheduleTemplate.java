package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.threeten.extra.LocalDateRange;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "planningMassTemplates"})
public class ScheduleTemplate {

    private List<ServiceType> serviceTypes;

    private List<PlanningMassTemplate> planningMassTemplates;

    /**
     * Noarg public constructor making the class instantiatable for JAXB.
     */
    public ScheduleTemplate() {
    }

    public ScheduleTemplate(List<PlanningMassTemplate> planningMassTemplates) {
        this.planningMassTemplates = planningMassTemplates.stream()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        serviceTypes = planningMassTemplates.parallelStream()
                .flatMap(planningMassTemplate -> planningMassTemplate.getServiceTypeCounts().keySet().parallelStream())
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    public LocalDateRange getDateRange() {
        final LocalDate start = Collections.min(planningMassTemplates).getDateTime().toLocalDate();
        final LocalDate endInclusive = Collections.max(planningMassTemplates).getDateTime().toLocalDate();
        return LocalDateRange.ofClosed(start, endInclusive);
    }

    @XmlElementWrapper(name = "serviceTypes")
    @XmlElement(name = "serviceType")
    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @XmlElementWrapper(name = "planningMassTemplates")
    @XmlElement(name = "planningMassTemplate")
    public List<PlanningMassTemplate> getPlanningMassTemplates() {
        return planningMassTemplates;
    }

    public void setPlanningMassTemplates(List<PlanningMassTemplate> planningMassTemplates) {
        this.planningMassTemplates = planningMassTemplates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleTemplate that = (ScheduleTemplate) o;
        return Objects.equals(serviceTypes, that.serviceTypes) &&
                Objects.equals(planningMassTemplates, that.planningMassTemplates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypes, planningMassTemplates);
    }

}
