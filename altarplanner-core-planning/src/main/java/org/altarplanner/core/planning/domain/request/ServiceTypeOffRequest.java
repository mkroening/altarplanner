package org.altarplanner.core.planning.domain.request;

import java.util.AbstractMap;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.planning.Server;

public class ServiceTypeOffRequest extends AbstractMap.SimpleImmutableEntry<Server, ServiceType> {
  public ServiceTypeOffRequest(Server key, ServiceType value) {
    super(key, value);
  }
}
