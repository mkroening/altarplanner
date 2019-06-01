package org.altarplanner.core.persistence.jaxb.domain;

import org.altarplanner.core.planning.domain.ServiceType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ServiceTypeXmlAdapter extends XmlAdapter<ServiceTypeBean, ServiceType> {
  @Override
  public ServiceType unmarshal(ServiceTypeBean serviceTypeBean) {
    final var serviceType = new ServiceType();
    serviceType.setName(serviceTypeBean.getName());
    serviceType.setMaxYear(serviceTypeBean.getMaxYear());
    serviceType.setMinYear(serviceTypeBean.getMinYear());
    return serviceType;
  }

  @Override
  public ServiceTypeBean marshal(ServiceType serviceType) {
    final var serviceTypeBean = new ServiceTypeBean();
    serviceTypeBean.setName(serviceType.getName());
    serviceTypeBean.setMaxYear(serviceType.getMaxYear());
    serviceTypeBean.setMinYear(serviceType.getMinYear());
    serviceTypeBean.setXmlID(
        serviceType.getName() + "_" + serviceType.getMaxYear() + "-" + serviceType.getMinYear());
    return serviceTypeBean;
  }
}
