package org.altarplanner.app.config;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.xlsx.PoiIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerImporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerImporter.class);
  @FXML private ChoiceBox<String> surnameHeadingChoiceBox;
  @FXML private ChoiceBox<String> forenameHeadingChoiceBox;
  @FXML private ChoiceBox<String> yearHeadingChoiceBox;
  private Consumer<List<Server>> serversConsumer;
  private File inputFile;

  public void setServersConsumer(Consumer<List<Server>> serversConsumer) {
    this.serversConsumer = serversConsumer;
  }

  public void setInputFile(File inputFile) {
    this.inputFile = inputFile;
    final List<String> header = PoiIO.readHeader(inputFile);
    surnameHeadingChoiceBox.getItems().setAll(header);
    surnameHeadingChoiceBox.getSelectionModel().select(0);
    forenameHeadingChoiceBox.getItems().setAll(header);
    forenameHeadingChoiceBox.getSelectionModel().select(1);
    yearHeadingChoiceBox.getItems().setAll(header);
    yearHeadingChoiceBox.getSelectionModel().select(2);
  }

  @FXML
  private void importServers() {
    try {
      serversConsumer.accept(
          PoiIO.readServers(
              inputFile,
              surnameHeadingChoiceBox.getSelectionModel().getSelectedIndex(),
              forenameHeadingChoiceBox.getSelectionModel().getSelectedIndex(),
              yearHeadingChoiceBox.getSelectionModel().getSelectedIndex()));
      ((Stage) surnameHeadingChoiceBox.getScene().getWindow()).close();
    } catch (final IllegalStateException e) {
      LOGGER.debug("Unable to parse cells", e);
      LOGGER.error("Unable to parse cells");
      LOGGER.error("Required cell types:");
      LOGGER.error("  Surname: STRING");
      LOGGER.error("  Forename: STRING");
      LOGGER.error("  Year: NUMERIC");
    }
  }
}
