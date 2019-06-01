package org.altarplanner.core.persistence.jaxb.util;

import org.altarplanner.core.persistence.jaxb.domain.ServiceTypeXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;

import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class ServiceTypeXmlIDAdapter
    extends XmlAdapter<ServiceTypeXmlIDAdapter.ServiceTypeXmlIDREF, ServiceType> {
  public static class ServiceTypeXmlIDREF {
    @XmlIDREF
    @XmlValue
    @XmlJavaTypeAdapter(ServiceTypeXmlAdapter.class)
    public ServiceType serviceType;
  }

  @Override
  public ServiceType unmarshal(ServiceTypeXmlIDREF serviceTypeXmlIDREF) {
    return serviceTypeXmlIDREF.serviceType;
  }

  @Override
  public ServiceTypeXmlIDREF marshal(ServiceType serviceType) {
    final var serviceTypeXmlIDREF = new ServiceTypeXmlIDREF();
    serviceTypeXmlIDREF.serviceType = serviceType;
    return serviceTypeXmlIDREF;
  }
}
