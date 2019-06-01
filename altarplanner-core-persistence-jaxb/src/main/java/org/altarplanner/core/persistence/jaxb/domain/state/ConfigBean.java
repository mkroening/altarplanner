package org.altarplanner.core.persistence.jaxb.domain.state;

import org.altarplanner.core.persistence.jaxb.domain.request.PairRequestXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.RegularMass;
import org.altarplanner.core.planning.domain.planning.Server;
import org.altarplanner.core.planning.domain.request.PairRequest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "config")
@XmlType(propOrder = {"serviceTypes", "servers", "pairs", "regularMasses"})
public class ConfigBean implements Serializable {
  private List<ServiceType> serviceTypes;

  private List<Server> servers;

  private List<PairRequest> pairs;

  private List<RegularMass> regularMasses;

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

  @XmlElementWrapper(name = "regularMasses")
  @XmlElement(name = "regularMass")
  public List<RegularMass> getRegularMasses() {
    return regularMasses;
  }

  public void setRegularMasses(List<RegularMass> regularMasses) {
    this.regularMasses = regularMasses;
  }
}
