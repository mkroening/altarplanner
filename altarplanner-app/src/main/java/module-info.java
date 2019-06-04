module org.altarplanner.app {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;

  requires org.altarplanner.core.planning;
  requires org.altarplanner.core.persistence.jaxb;
  requires org.altarplanner.core.persistence.poi;

  requires slf4j.api;

  exports org.altarplanner.app to javafx.graphics;

  opens org.altarplanner.app to javafx.fxml;
  opens org.altarplanner.app.config to javafx.fxml;
  opens org.altarplanner.app.planning to javafx.fxml;
}
