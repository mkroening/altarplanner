package org.altarplanner.core.domain.util;

import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.mass.RegularMass;

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

    static RegularMass buildRegularMass(DayOfWeek day, LocalTime time, String church, String form, Map<ServiceType, Integer> serviceTypeCount) {
        RegularMass regularMass = new RegularMass();
        regularMass.setDay(day);
        regularMass.setTime(time);
        regularMass.setChurch(church);
        regularMass.setForm(form);
        regularMass.setServiceTypeCounts(serviceTypeCount);
        return regularMass;
    }

}
