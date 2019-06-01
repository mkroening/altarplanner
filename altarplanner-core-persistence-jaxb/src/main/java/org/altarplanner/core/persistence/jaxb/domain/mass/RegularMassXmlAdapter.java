package org.altarplanner.core.persistence.jaxb.domain.mass;

import org.altarplanner.core.planning.domain.mass.RegularMass;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RegularMassXmlAdapter extends XmlAdapter<RegularMassBean, RegularMass> {
  @Override
  public RegularMass unmarshal(RegularMassBean regularMassBean) {
    final var regularMass = new RegularMass();
    regularMass.setDay(regularMassBean.getDay());
    regularMass.setTime(regularMassBean.getTime());
    regularMass.setChurch(regularMassBean.getChurch());
    regularMass.setForm(regularMassBean.getForm());
    regularMass.setAnnotation(regularMassBean.getAnnotation());
    regularMass.setServiceTypeCounts(regularMassBean.getServiceTypeCounts());
    return regularMass;
  }

  @Override
  public RegularMassBean marshal(RegularMass regularMass) {
    final var regularMassBean = new RegularMassBean();
    regularMassBean.setDay(regularMass.getDay());
    regularMassBean.setTime(regularMass.getTime());
    regularMassBean.setChurch(regularMass.getChurch());
    regularMassBean.setForm(regularMass.getForm());
    regularMassBean.setAnnotation(regularMass.getAnnotation());
    regularMassBean.setServiceTypeCounts(regularMass.getServiceTypeCounts());
    return regularMassBean;
  }
}
