package org.altarplanner.core.persistence.jaxb.domain.planning;

import org.altarplanner.core.persistence.jaxb.domain.ServiceTypeXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.planning.Server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

@XmlType(propOrder = {"type", "server"})
public class ServiceBean implements Serializable {
  private ServiceType type;

  private Server server;

  @XmlIDREF
  @XmlAttribute
  @XmlJavaTypeAdapter(ServiceTypeXmlAdapter.class)
  public ServiceType getType() {
    return type;
  }

  public void setType(ServiceType type) {
    this.type = type;
  }

  @XmlIDREF
  @XmlAttribute
  @XmlJavaTypeAdapter(ServerXmlAdapter.class)
  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }
}
