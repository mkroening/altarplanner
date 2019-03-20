module org.altarplanner.core {
  requires java.xml;
  requires java.xml.bind;
  requires jaxb.java.time.adapters;
  requires org.optaplanner.core;
  requires org.optaplanner.persistence.jaxb;
  requires org.threeten.extra;
  requires poi;
  requires poi.ooxml;
  requires slf4j.api;

  exports org.altarplanner.core.domain;
  exports org.altarplanner.core.domain.mass;
  exports org.altarplanner.core.domain.planning;
  exports org.altarplanner.core.domain.request;
  exports org.altarplanner.core.domain.state;
  exports org.altarplanner.core.solver;
  exports org.altarplanner.core.util;
  exports org.altarplanner.core.xlsx;

  exports org.altarplanner.core.xml.jaxb.util to com.sun.xml.bind;

  opens org.altarplanner.core.domain.state to java.xml.bind, org.optaplanner.core;
  opens org.altarplanner.core.domain.mass to org.optaplanner.core;
  opens org.altarplanner.core.domain.planning to org.optaplanner.core;
  opens org.altarplanner.core.solver;
}
