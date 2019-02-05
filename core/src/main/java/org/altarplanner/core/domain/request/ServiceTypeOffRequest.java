package org.altarplanner.core.domain.request;

import java.util.AbstractMap;
import org.altarplanner.core.domain.planning.Server;
import org.altarplanner.core.domain.ServiceType;

public class ServiceTypeOffRequest extends AbstractMap.SimpleImmutableEntry<Server, ServiceType> {
  public ServiceTypeOffRequest(Server key, ServiceType value) {
    super(key, value);
  }
}
