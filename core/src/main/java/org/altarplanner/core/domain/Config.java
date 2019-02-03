package org.altarplanner.core.domain;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.xml.StrictJAXB;
import org.altarplanner.core.xml.jaxb.util.PairRequestXmlAdapter;
import org.threeten.extra.LocalDateRange;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "regularMasses", "servers", "pairs"})
public class Config implements Serializable {

  public static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle("org.altarplanner.core.locale.locale");

  private List<ServiceType> serviceTypes;
  private List<RegularMass> regularMasses;
  private List<Server> servers;
  private List<PairRequest> pairs;

  public Config() {
    this.serviceTypes = new ArrayList<>();
    this.regularMasses = new ArrayList<>();
    this.servers = new ArrayList<>();
    this.pairs = new ArrayList<>();
  }

  public Config(Config other) {
    this.serviceTypes = other.serviceTypes;
    this.regularMasses = other.regularMasses;
    this.servers = other.servers;
    this.pairs = other.pairs;
  }

  public static Config unmarshal(Path input) throws UnmarshalException {
    return StrictJAXB.unmarshal(input, Config.class);
  }

  public void marshal(Path output) {
    StrictJAXB.marshal(this, output);
  }

  public Stream<PlanningMassTemplate> getPlanningMassTemplateStreamFromRegularMassesIn(
      LocalDateRange dateRange) {
    Map<DayOfWeek, List<RegularMass>> dayMassMap =
        regularMasses.stream().collect(Collectors.groupingBy(RegularMass::getDay));
    return dateRange.stream()
        .flatMap(
            date ->
                Optional.ofNullable(dayMassMap.get(date.getDayOfWeek())).stream()
                    .flatMap(
                        masses ->
                            masses.stream().map(mass -> new PlanningMassTemplate(mass, date))));
  }

  public void remove(final ServiceType serviceType) {
    regularMasses.forEach(regularMass -> regularMass.getServiceTypeCounts().remove(serviceType));
    servers.forEach(server -> server.getInabilities().remove(serviceType));
  }

  public List<Server> getPairedWith(Server server) {
    return pairs
        .parallelStream()
        .filter(pairRequest -> pairRequest.getKey() == server)
        .map(PairRequest::getValue)
        .collect(Collectors.toUnmodifiableList());
  }

  public void addPair(PairRequest pair) {
    pairs.add(pair);
    pairs.add(new PairRequest(pair.getValue(), pair.getKey()));
  }

  public void removePair(PairRequest pair) {
    pairs.remove(pair);
    pairs.remove(new PairRequest(pair.getValue(), pair.getKey()));
  }

  public void removeAllPairsWith(Server server) {
    pairs.removeIf(
        pairRequest -> pairRequest.getKey() == server || pairRequest.getValue() == server);
  }

  @XmlElementWrapper(name = "serviceTypes")
  @XmlElement(name = "serviceType")
  public List<ServiceType> getServiceTypes() {
    return serviceTypes;
  }

  public void setServiceTypes(List<ServiceType> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }

  @XmlElementWrapper(name = "regularMasses")
  @XmlElement(name = "regularMass")
  public List<RegularMass> getRegularMasses() {
    return regularMasses;
  }

  public void setRegularMasses(List<RegularMass> regularMasses) {
    this.regularMasses = regularMasses;
  }

  @XmlElementWrapper(name = "servers")
  @XmlElement(name = "server")
  public List<Server> getServers() {
    return servers;
  }

  public void setServers(List<Server> servers) {
    this.servers = servers;
  }

  @XmlElementWrapper(name = "pairs")
  @XmlElement(name = "pair")
  @XmlJavaTypeAdapter(PairRequestXmlAdapter.class)
  public List<PairRequest> getPairs() {
    return pairs;
  }

  public void setPairs(List<PairRequest> pairs) {
    this.pairs = pairs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Config config = (Config) o;
    return Objects.equals(serviceTypes, config.serviceTypes)
        && Objects.equals(regularMasses, config.regularMasses)
        && Objects.equals(servers, config.servers)
        && Objects.equals(pairs, config.pairs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceTypes, regularMasses, servers, pairs);
  }
}
