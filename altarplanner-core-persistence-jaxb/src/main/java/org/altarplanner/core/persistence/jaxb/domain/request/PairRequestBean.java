package org.altarplanner.core.persistence.jaxb.domain.request;

import org.altarplanner.core.planning.domain.planning.Server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(propOrder = {"server", "pairedWith"})
public class PairRequestBean implements Serializable {
  private Server server;

  private Server pairedWith;

  @XmlIDREF
  @XmlAttribute
  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  @XmlIDREF
  @XmlAttribute
  public Server getPairedWith() {
    return pairedWith;
  }

  public void setPairedWith(Server pairedWith) {
    this.pairedWith = pairedWith;
  }
}
