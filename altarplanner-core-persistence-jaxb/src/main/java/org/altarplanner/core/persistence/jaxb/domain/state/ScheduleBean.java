package org.altarplanner.core.persistence.jaxb.domain.state;

import io.github.threetenjaxb.core.LocalDateXmlAdapter;
import org.altarplanner.core.persistence.jaxb.domain.ServiceTypeXmlAdapter;
import org.altarplanner.core.persistence.jaxb.domain.mass.PlanningMassXmlAdapter;
import org.altarplanner.core.persistence.jaxb.domain.planning.ServerXmlAdapter;
import org.altarplanner.core.persistence.jaxb.domain.request.PairRequestXmlAdapter;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.PlanningMass;
import org.altarplanner.core.planning.domain.planning.Server;
import org.altarplanner.core.planning.domain.request.PairRequest;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jaxb.api.score.buildin.hardsoft.HardSoftScoreJaxbXmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name = "schedule")
@XmlType(
    propOrder = {
      "serviceTypes",
      "servers",
      "pairs",
      "publishedMasses",
      "finalDraftMasses",
      "futureDraftMasses",
      "feastDays",
      "score"
    })
public class ScheduleBean implements Serializable {
  private List<ServiceType> serviceTypes;

  private List<Server> servers;

  private List<PairRequest> pairs;

  private List<PlanningMass> publishedMasses;

  private List<PlanningMass> finalDraftMasses;

  private List<PlanningMass> futureDraftMasses;

  private List<LocalDate> feastDays;

  private HardSoftScore score;

  @XmlElementWrapper(name = "serviceTypes")
  @XmlElement(name = "serviceType")
  @XmlJavaTypeAdapter(ServiceTypeXmlAdapter.class)
  public List<ServiceType> getServiceTypes() {
    return serviceTypes;
  }

  public void setServiceTypes(List<ServiceType> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }

  @XmlElementWrapper(name = "servers")
  @XmlElement(name = "server")
  @XmlJavaTypeAdapter(ServerXmlAdapter.class)
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

  @XmlElementWrapper(name = "publishedMasses")
  @XmlElement(name = "planningMass")
  @XmlJavaTypeAdapter(PlanningMassXmlAdapter.class)
  public List<PlanningMass> getPublishedMasses() {
    return publishedMasses;
  }

  public void setPublishedMasses(List<PlanningMass> publishedMasses) {
    this.publishedMasses = publishedMasses;
  }

  @XmlElementWrapper(name = "finalDraftMasses")
  @XmlElement(name = "planningMass")
  @XmlJavaTypeAdapter(PlanningMassXmlAdapter.class)
  public List<PlanningMass> getFinalDraftMasses() {
    return finalDraftMasses;
  }

  public void setFinalDraftMasses(List<PlanningMass> finalDraftMasses) {
    this.finalDraftMasses = finalDraftMasses;
  }

  @XmlElementWrapper(name = "futureDraftMasses")
  @XmlElement(name = "planningMass")
  @XmlJavaTypeAdapter(PlanningMassXmlAdapter.class)
  public List<PlanningMass> getFutureDraftMasses() {
    return futureDraftMasses;
  }

  public void setFutureDraftMasses(List<PlanningMass> futureDraftMasses) {
    this.futureDraftMasses = futureDraftMasses;
  }

  @XmlList
  @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
  public List<LocalDate> getFeastDays() {
    return feastDays;
  }

  public void setFeastDays(List<LocalDate> feastDays) {
    this.feastDays = feastDays;
  }

  @XmlJavaTypeAdapter(HardSoftScoreJaxbXmlAdapter.class)
  public HardSoftScore getScore() {
    return score;
  }

  public void setScore(HardSoftScore score) {
    this.score = score;
  }
}
