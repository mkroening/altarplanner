module org.altarplanner.core.planning {
  requires org.optaplanner.core;
  requires transitive org.threeten.extra;
  requires org.slf4j;

  exports org.altarplanner.core.planning.domain;
  exports org.altarplanner.core.planning.domain.mass;
  exports org.altarplanner.core.planning.domain.planning;
  exports org.altarplanner.core.planning.domain.request;
  exports org.altarplanner.core.planning.domain.state;
  exports org.altarplanner.core.planning.solver;
  exports org.altarplanner.core.planning.util;

  opens org.altarplanner.core.planning.domain.state to org.optaplanner.core;
  opens org.altarplanner.core.planning.domain.mass to org.optaplanner.core;
  opens org.altarplanner.core.planning.domain.planning to org.optaplanner.core;
  opens org.altarplanner.core.planning.solver;
}
