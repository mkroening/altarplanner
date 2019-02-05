package org.altarplanner.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.xml.jaxb.util.PairRequestXmlAdapter;

@XmlType(propOrder = {"serviceTypes", "servers", "pairs"})
public abstract class ServerAware {

  private List<ServiceType> serviceTypes;
  private List<Server> servers;
  private List<PairRequest> pairs;

  public ServerAware() {
    this.serviceTypes = new ArrayList<>();
    this.servers = new ArrayList<>();
    this.pairs = new ArrayList<>();
  }

  public ServerAware(ServerAware other) {
    this.serviceTypes = other.serviceTypes;
    this.servers = other.servers;
    this.pairs = other.pairs;
  }

  public void remove(final ServiceType serviceType) {
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
    ServerAware that = (ServerAware) o;
    return serviceTypes.equals(that.serviceTypes)
        && servers.equals(that.servers)
        && pairs.equals(that.pairs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceTypes, servers, pairs);
  }
}
