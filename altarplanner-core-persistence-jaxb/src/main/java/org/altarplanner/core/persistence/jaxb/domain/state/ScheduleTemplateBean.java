package org.altarplanner.core.persistence.jaxb.domain.state;

import io.github.threetenjaxb.core.LocalDateXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name = "scheduleTemplate")
@XmlType(propOrder = {"serviceTypes", "planningMassTemplates", "feastDays"})
public class ScheduleTemplateBean implements Serializable {
    private List<ServiceType> serviceTypes;

    private List<PlanningMassTemplate> planningMassTemplates;

    private List<LocalDate> feastDays;

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

    @XmlList
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public List<LocalDate> getFeastDays() {
        return feastDays;
    }

    public void setFeastDays(List<LocalDate> feastDays) {
        this.feastDays = feastDays;
    }
}
