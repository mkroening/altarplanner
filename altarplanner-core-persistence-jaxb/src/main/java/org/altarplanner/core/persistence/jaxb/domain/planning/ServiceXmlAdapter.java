package org.altarplanner.core.persistence.jaxb.domain.planning;

import org.altarplanner.core.planning.domain.planning.Service;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ServiceXmlAdapter extends XmlAdapter<ServiceBean, Service> {
  @Override
  public Service unmarshal(ServiceBean serviceBean) {
    final var service = new Service();
    service.setType(serviceBean.getType());
    service.setServer(serviceBean.getServer());
    return service;
  }

  @Override
  public ServiceBean marshal(Service service) {
    final var serviceBean = new ServiceBean();
    serviceBean.setType(service.getType());
    serviceBean.setServer(service.getServer());
    return serviceBean;
  }
}
