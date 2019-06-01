package org.altarplanner.core.persistence.jaxb.domain.state;

import org.altarplanner.core.planning.domain.state.Schedule;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ScheduleXmlAdapter extends XmlAdapter<ScheduleBean, Schedule> {
    @Override
    public Schedule unmarshal(ScheduleBean scheduleBean) {
        final var schedule = new Schedule();
        schedule.setServiceTypes(scheduleBean.getServiceTypes());
        schedule.setServers(scheduleBean.getServers());
        schedule.setPairs(scheduleBean.getPairs());
        schedule.setPublishedMasses(scheduleBean.getPublishedMasses());
        schedule.setFinalDraftMasses(scheduleBean.getFinalDraftMasses());
        schedule.setFutureDraftMasses(scheduleBean.getFutureDraftMasses());
        schedule.updateServiceMassReferences();
        schedule.updatePlanningIds();
        schedule.updateMassIsPinned();
        schedule.setFeastDays(scheduleBean.getFeastDays());
        schedule.setScore(scheduleBean.getScore());
        return schedule;
    }

    @Override
    public ScheduleBean marshal(Schedule schedule) {
        final var scheduleBean = new ScheduleBean();
        scheduleBean.setServiceTypes(schedule.getServiceTypes());
        scheduleBean.setServers(schedule.getServers());
        scheduleBean.setPairs(schedule.getPairs());
        scheduleBean.setPublishedMasses(schedule.getPublishedMasses());
        scheduleBean.setFinalDraftMasses(schedule.getFinalDraftMasses());
        scheduleBean.setFutureDraftMasses(schedule.getFutureDraftMasses());
        scheduleBean.setFeastDays(schedule.getFeastDays());
        scheduleBean.setScore(schedule.getScore());
        return scheduleBean;
    }
}
