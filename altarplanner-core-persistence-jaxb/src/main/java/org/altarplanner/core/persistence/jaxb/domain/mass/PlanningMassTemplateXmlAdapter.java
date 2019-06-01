package org.altarplanner.core.persistence.jaxb.domain.mass;

import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PlanningMassTemplateXmlAdapter
    extends XmlAdapter<PlanningMassTemplateBean, PlanningMassTemplate> {
  @Override
  public PlanningMassTemplate unmarshal(PlanningMassTemplateBean planningMassTemplateBean) {
    final var planningMassTemplate = new PlanningMassTemplate();
    planningMassTemplate.setDateTime(planningMassTemplateBean.getDateTime());
    planningMassTemplate.setChurch(planningMassTemplateBean.getChurch());
    planningMassTemplate.setForm(planningMassTemplateBean.getForm());
    planningMassTemplate.setAnnotation(planningMassTemplateBean.getAnnotation());
    planningMassTemplate.setServiceTypeCounts(planningMassTemplateBean.getServiceTypeCounts());
    return planningMassTemplate;
  }

  @Override
  public PlanningMassTemplateBean marshal(PlanningMassTemplate planningMassTemplate) {
    final var planningMassTemplateBean = new PlanningMassTemplateBean();
    planningMassTemplateBean.setDateTime(planningMassTemplate.getDateTime());
    planningMassTemplateBean.setChurch(planningMassTemplate.getChurch());
    planningMassTemplateBean.setForm(planningMassTemplate.getForm());
    planningMassTemplateBean.setAnnotation(planningMassTemplate.getAnnotation());
    planningMassTemplateBean.setServiceTypeCounts(planningMassTemplate.getServiceTypeCounts());
    return planningMassTemplateBean;
  }
}
