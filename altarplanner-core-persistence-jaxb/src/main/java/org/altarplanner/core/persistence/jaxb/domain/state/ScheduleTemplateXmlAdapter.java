package org.altarplanner.core.persistence.jaxb.domain.state;

import org.altarplanner.core.planning.domain.state.ScheduleTemplate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ScheduleTemplateXmlAdapter extends XmlAdapter<ScheduleTemplateBean, ScheduleTemplate> {
    @Override
    public ScheduleTemplate unmarshal(ScheduleTemplateBean scheduleTemplateBean) {
        final var scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setServiceTypes(scheduleTemplateBean.getServiceTypes());
        scheduleTemplate.setPlanningMassTemplates(scheduleTemplateBean.getPlanningMassTemplates());
        scheduleTemplate.setFeastDays(scheduleTemplateBean.getFeastDays());
        return scheduleTemplate;
    }

    @Override
    public ScheduleTemplateBean marshal(ScheduleTemplate scheduleTemplate) {
        final var scheduleTemplateBean = new ScheduleTemplateBean();
        scheduleTemplateBean.setServiceTypes(scheduleTemplate.getServiceTypes());
        scheduleTemplateBean.setPlanningMassTemplates(scheduleTemplate.getPlanningMassTemplates());
        scheduleTemplateBean.setFeastDays(scheduleTemplate.getFeastDays());
        return scheduleTemplateBean;
    }
}
