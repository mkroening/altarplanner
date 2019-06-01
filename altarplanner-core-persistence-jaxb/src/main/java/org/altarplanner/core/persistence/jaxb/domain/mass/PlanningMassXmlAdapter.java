package org.altarplanner.core.persistence.jaxb.domain.mass;

import org.altarplanner.core.planning.domain.mass.PlanningMass;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PlanningMassXmlAdapter extends XmlAdapter<PlanningMassBean, PlanningMass> {
  @Override
  public PlanningMass unmarshal(PlanningMassBean planningMassBean) {
    final var planningMass = new PlanningMass();
    planningMass.setDateTime(planningMassBean.getDateTime());
    planningMass.setChurch(planningMassBean.getChurch());
    planningMass.setForm(planningMassBean.getForm());
    planningMass.setAnnotation(planningMassBean.getAnnotation());
    planningMass.setServices(planningMassBean.getServices());
    return planningMass;
  }

  @Override
  public PlanningMassBean marshal(PlanningMass planningMass) {
    final var planningMassBean = new PlanningMassBean();
    planningMassBean.setDateTime(planningMass.getDateTime());
    planningMassBean.setChurch(planningMass.getChurch());
    planningMassBean.setForm(planningMass.getForm());
    planningMassBean.setAnnotation(planningMass.getAnnotation());
    planningMassBean.setServices(planningMass.getServices());
    return planningMassBean;
  }
}
