package org.altarplanner.core.xml.jaxb.util;

import org.altarplanner.core.domain.ServiceType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<ServiceType, Integer> map = new HashMap<>();
        for(Entry entry : v.entry) {
            map.put(entry.serviceType, entry.count);
        }
        return map;
    }

    @Override
    public AdaptedMap marshal(Map<ServiceType, Integer> v) {
        AdaptedMap adaptedMap = new AdaptedMap();
        for(Map.Entry<ServiceType, Integer> mapEntry : v.entrySet()) {
            Entry entry = new Entry();
            entry.serviceType = mapEntry.getKey();
            entry.count = mapEntry.getValue();
            adaptedMap.entry.add(entry);
        }
        return adaptedMap;
    }
}
