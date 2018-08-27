package org.altarplanner.core.xml.jaxb.util;

import org.altarplanner.core.domain.ServiceType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceTypeCountXmlAdapter extends XmlAdapter<ServiceTypeCountXmlAdapter.AdaptedMap, Map<ServiceType, Integer>> {
    public static class AdaptedMap {
        public List<Entry> entry = new ArrayList<>();
    }

    public static class Entry {
        @XmlAttribute
        @XmlIDREF
        public ServiceType serviceType;
        @XmlAttribute
        public int count;
    }

    @Override
    public Map<ServiceType, Integer> unmarshal(AdaptedMap v) {
        return v.entry.stream()
                .collect(Collectors.toUnmodifiableMap(entry -> entry.serviceType, entry -> entry.count));
    }

    @Override
    public AdaptedMap marshal(Map<ServiceType, Integer> v) {
        AdaptedMap adaptedMap = new AdaptedMap();
        adaptedMap.entry = v.entrySet().stream()
                .map(mapEntry -> {
                    Entry entry = new Entry();
                    entry.serviceType = mapEntry.getKey();
                    entry.count = mapEntry.getValue();
                    return entry;
                })
                .sorted(Comparator.comparing(entry -> entry.serviceType, ServiceType.getDescComparator()))
                .collect(Collectors.toUnmodifiableList());
        return adaptedMap;
    }
}
