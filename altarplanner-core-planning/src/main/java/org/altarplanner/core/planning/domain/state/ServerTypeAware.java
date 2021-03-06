package org.altarplanner.core.planning.domain.state;

import java.util.ArrayList;
import java.util.List;
import org.altarplanner.core.planning.domain.ServiceType;

public abstract class ServerTypeAware {

  private List<ServiceType> serviceTypes;

  public ServerTypeAware() {
    this.serviceTypes = new ArrayList<>();
  }

  public ServerTypeAware(ServerTypeAware other) {
    this.serviceTypes = other.serviceTypes;
  }

  public List<ServiceType> getServiceTypes() {
    return serviceTypes;
  }

  public void setServiceTypes(List<ServiceType> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }
}
