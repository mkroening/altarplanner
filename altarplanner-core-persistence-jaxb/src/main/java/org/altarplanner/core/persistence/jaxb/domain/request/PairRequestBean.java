package org.altarplanner.core.persistence.jaxb.domain.request;

import org.altarplanner.core.persistence.jaxb.domain.planning.ServerXmlAdapter;
import org.altarplanner.core.planning.domain.planning.Server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

@XmlType(propOrder = {"server", "pairedWith"})
public class PairRequestBean implements Serializable {
  private Server server;

  private Server pairedWith;

  @XmlIDREF
  @XmlAttribute
  @XmlJavaTypeAdapter(ServerXmlAdapter.class)
  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  @XmlIDREF
  @XmlAttribute
  @XmlJavaTypeAdapter(ServerXmlAdapter.class)
  public Server getPairedWith() {
    return pairedWith;
  }

  public void setPairedWith(Server pairedWith) {
    this.pairedWith = pairedWith;
  }
}
