package org.altarplanner.core.persistence.jaxb.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.altarplanner.core.persistence.jaxb.domain.ServiceTypeXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;

public class ServiceTypeCountsXmlAdapter
    extends XmlAdapter<ServiceTypeCountsXmlAdapter.AdaptedMap, Map<ServiceType, Integer>> {
  public static class AdaptedMap {
    @XmlElement(name = "serviceTypeCount")
    public List<Entry> entry = new ArrayList<>();
  }

  public static class Entry {
    @XmlJavaTypeAdapter(ServiceTypeXmlAdapter.class)
    @XmlAttribute
    @XmlIDREF
    public ServiceType serviceType;

    @XmlAttribute public int count;
  }

  @Override
  public Map<ServiceType, Integer> unmarshal(AdaptedMap v) {
    return v.entry.stream()
        .collect(Collectors.toMap(entry -> entry.serviceType, entry -> entry.count));
  }

  @Override
  public AdaptedMap marshal(Map<ServiceType, Integer> v) {
    AdaptedMap adaptedMap = new AdaptedMap();
    adaptedMap.entry =
        v.entrySet().stream()
            .map(
                mapEntry -> {
                  Entry entry = new Entry();
                  entry.serviceType = mapEntry.getKey();
                  entry.count = mapEntry.getValue();
                  return entry;
                })
            .sorted(Comparator.comparing(entry -> entry.serviceType))
            .collect(Collectors.toUnmodifiableList());
    return adaptedMap;
  }
}
