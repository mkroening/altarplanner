module org.altarplanner.core.persistence.jaxb {
  requires transitive org.altarplanner.core.planning;
  requires transitive java.xml.bind;
  requires org.optaplanner.persistence.jaxb;
  requires io.github.threetenjaxb.core;

  exports org.altarplanner.core.persistence.jaxb;
  exports org.altarplanner.core.persistence.jaxb.domain;
  exports org.altarplanner.core.persistence.jaxb.domain.mass;
  exports org.altarplanner.core.persistence.jaxb.domain.planning;
  exports org.altarplanner.core.persistence.jaxb.domain.request;
  exports org.altarplanner.core.persistence.jaxb.domain.state;
  exports org.altarplanner.core.persistence.jaxb.util;

  opens org.altarplanner.core.persistence.jaxb.domain.request to
      java.xml.bind;
  opens org.altarplanner.core.persistence.jaxb.domain.state to
      java.xml.bind;
}
