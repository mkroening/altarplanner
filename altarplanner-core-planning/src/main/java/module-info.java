module org.altarplanner.core.planning {
  requires transitive org.optaplanner.core;
  requires org.threeten.extra;
  requires poi;
  requires poi.ooxml;
  requires slf4j.api;

  exports org.altarplanner.core.planning.domain;
  exports org.altarplanner.core.planning.domain.mass;
  exports org.altarplanner.core.planning.domain.planning;
  exports org.altarplanner.core.planning.domain.request;
  exports org.altarplanner.core.planning.domain.state;
  exports org.altarplanner.core.planning.solver;
  exports org.altarplanner.core.planning.util;
  exports org.altarplanner.core.planning.xlsx;

  opens org.altarplanner.core.planning.domain.state to org.optaplanner.core;
  opens org.altarplanner.core.planning.domain.mass to org.optaplanner.core;
  opens org.altarplanner.core.planning.domain.planning to org.optaplanner.core;
  opens org.altarplanner.core.planning.solver;
}
