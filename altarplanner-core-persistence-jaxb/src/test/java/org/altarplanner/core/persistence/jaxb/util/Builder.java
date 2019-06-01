package org.altarplanner.core.persistence.jaxb.util;

import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.RegularMass;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

class Builder {

  static ServiceType buildServiceType(String name, int maxYear, int minYear) {
    ServiceType serviceType = new ServiceType();
    serviceType.setName(name);
    serviceType.setMaxYear(maxYear);
    serviceType.setMinYear(minYear);
    return serviceType;
  }

  static RegularMass buildRegularMass(
      DayOfWeek day,
      LocalTime time,
      String church,
      String form,
      Map<ServiceType, Integer> serviceTypeCount,
      String annotation) {
    RegularMass regularMass = new RegularMass();
    regularMass.setDay(day);
    regularMass.setTime(time);
    regularMass.setChurch(church);
    regularMass.setForm(form);
    regularMass.setAnnotation(annotation);
    regularMass.setServiceTypeCounts(serviceTypeCount);
    return regularMass;
  }
}
