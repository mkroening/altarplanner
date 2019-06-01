package org.altarplanner.core.planning.domain.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.planning.Server;
import org.altarplanner.core.planning.domain.request.PairRequest;

public abstract class ServerAware extends ServerTypeAware {

  private List<Server> servers;
  private List<PairRequest> pairs;

  public ServerAware() {
    super();
    this.servers = new ArrayList<>();
    this.pairs = new ArrayList<>();
  }

  public ServerAware(ServerAware other) {
    super(other);
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

  public List<Server> getServers() {
    return servers;
  }

  public void setServers(List<Server> servers) {
    this.servers = servers;
  }

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
    return servers.equals(that.servers) && pairs.equals(that.pairs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(servers, pairs);
  }
}
