package org.altarplanner.core.domain.mass;

import java.util.Map;
import org.altarplanner.core.domain.ServiceType;

public interface TemplateMass extends GenericMass {

  Map<ServiceType, Integer> getServiceTypeCounts();

  void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts);
}
