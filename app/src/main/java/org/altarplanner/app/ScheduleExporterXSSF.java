package org.altarplanner.app;

import java.io.IOException;
import java.nio.file.Path;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.altarplanner.core.domain.state.Schedule;
import org.altarplanner.core.xlsx.XSSF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleExporterXSSF {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleExporterXSSF.class);

  @FXML private Label compactFormatColumnCountLabel;
  @FXML private TextField compactFormatColumnCountNumericField;
  @FXML private ChoiceBox<String> formatChoiceBox;

  private ObjectProperty<Integer> compactFormatColumnCountProperty;
  private Schedule schedule;
  private Path output;

  @FXML
  private void initialize() {
    formatChoiceBox
        .getItems()
        .setAll(
            Launcher.RESOURCE_BUNDLE.getString("scheduleExporterXSSF.formatChoiceBox.compact"),
            Launcher.RESOURCE_BUNDLE.getString("scheduleExporterXSSF.formatChoiceBox.overview"));
    formatChoiceBox.getSelectionModel().selectFirst();

    compactFormatColumnCountLabel
        .visibleProperty()
        .bind(formatChoiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(0));
    compactFormatColumnCountNumericField
        .visibleProperty()
        .bind(formatChoiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(0));

    final var textFormatter =
        new TextFormatter<>(
            new IntegerStringConverter(),
            3,
            change -> {
              if (change.getText().matches("[0-9]*")) {
                return change;
              } else {
                return null;
              }
            });

    compactFormatColumnCountNumericField.setTextFormatter(textFormatter);
    compactFormatColumnCountProperty = textFormatter.valueProperty();
  }

  public void setSchedule(final Schedule schedule) {
    this.schedule = schedule;
  }

  public void setOutput(final Path output) {
    this.output = output;
  }

  @FXML
  private void exportSchedule() throws IOException {
    switch (formatChoiceBox.getSelectionModel().getSelectedIndex()) {
      case 0:
        XSSF.exportScheduleCompact(schedule, output, compactFormatColumnCountProperty.getValue());
        break;
      case 1:
        XSSF.exportScheduleOverview(schedule, output);
        break;
    }
    LOGGER.info("Schedule has been exported as {}", output);
    ((Stage) compactFormatColumnCountLabel.getScene().getWindow()).close();
  }
}
