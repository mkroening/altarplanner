module org.altarplanner.app {
  requires java.xml.bind;
  requires org.controlsfx.controls;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires org.threeten.extra;
  requires slf4j.api;
  requires org.altarplanner.core.planning;
  requires org.altarplanner.core.persistence.jaxb;

  exports org.altarplanner.app to javafx.graphics;

  opens org.altarplanner.app to javafx.fxml;
  opens org.altarplanner.app.config to javafx.fxml;
  opens org.altarplanner.app.planning to javafx.fxml;
}
