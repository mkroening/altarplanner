package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.ServiceType;

import java.util.Map;

public interface TemplateMass extends GenericMass {

    Map<ServiceType, Integer> getServiceTypeCounts();

    void setServiceTypeCounts(Map<ServiceType, Integer> serviceTypeCounts);
}
