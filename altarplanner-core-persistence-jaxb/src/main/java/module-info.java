module org.altarplanner.core.persistence.jaxb {
  requires org.altarplanner.core.planning;
  requires java.xml.bind;
  requires org.threeten.extra;

  exports org.altarplanner.core.persistence.jaxb;
  exports org.altarplanner.core.persistence.jaxb.domain.request;
  exports org.altarplanner.core.persistence.jaxb.domain.state;

  opens org.altarplanner.core.persistence.jaxb.domain.request to
      java.xml.bind;
  opens org.altarplanner.core.persistence.jaxb.domain.state to
      java.xml.bind;
}
