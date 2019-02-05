package org.altarplanner.app.config;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.xlsx.PoiIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerImporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerImporter.class);
  @FXML private GridPane attributeColumnNameGridPane;
  @FXML private ChoiceBox<String> surnameHeadingChoiceBox;
  @FXML private ChoiceBox<String> forenameHeadingChoiceBox;
  @FXML private ChoiceBox<String> yearHeadingChoiceBox;
  @FXML private CheckBox importRegularAbsencesCheckBox;
  private Map<DayOfWeek, ChoiceBox<String>> absentOnDayOfWeekChoiceBoxes;
  private Map<Server, Server> servers;
  private Consumer<List<Server>> serversConsumer;
  private Path input;

  @FXML
  private void initialize() {
    final var rowIndex = attributeColumnNameGridPane.getRowCount() - 1;

    Arrays.stream(DayOfWeek.values())
        .forEach(
            dayOfWeek -> {
              final var pattern =
                  Launcher.RESOURCE_BUNDLE.getString(
                      "serverImporter.label.absentOnDayOfWeekLogicalValue");
              final var text =
                  MessageFormat.format(
                      pattern, dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()));
              final var label = new Label(text);
              label.disableProperty().bind(importRegularAbsencesCheckBox.selectedProperty().not());
              attributeColumnNameGridPane.add(label, 0, rowIndex + dayOfWeek.getValue());
            });

    absentOnDayOfWeekChoiceBoxes =
        Arrays.stream(DayOfWeek.values())
            .collect(
                Collectors.toUnmodifiableMap(
                    Function.identity(),
                    dayOfWeek -> {
                      final var choiceBox = new ChoiceBox<String>();
                      choiceBox
                          .disableProperty()
                          .bind(importRegularAbsencesCheckBox.selectedProperty().not());
                      return choiceBox;
                    }));

    absentOnDayOfWeekChoiceBoxes.forEach(
        (dayOfWeek, stringChoiceBox) ->
            attributeColumnNameGridPane.add(stringChoiceBox, 1, rowIndex + dayOfWeek.getValue()));
  }

  public void setServers(final List<Server> servers) {
    this.servers =
        servers.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
  }

  public void setServersConsumer(Consumer<List<Server>> serversConsumer) {
    this.serversConsumer = serversConsumer;
  }

  public void setInputFile(final Path input) {
    this.input = input;
    final List<String> header = PoiIO.readHeader(input);
    surnameHeadingChoiceBox.getItems().setAll(header);
    surnameHeadingChoiceBox.getSelectionModel().select(0);
    forenameHeadingChoiceBox.getItems().setAll(header);
    forenameHeadingChoiceBox.getSelectionModel().select(1);
    yearHeadingChoiceBox.getItems().setAll(header);
    yearHeadingChoiceBox.getSelectionModel().select(2);
    absentOnDayOfWeekChoiceBoxes.forEach(
        (dayOfWeek, stringChoiceBox) -> {
          stringChoiceBox.getItems().setAll(header);
          stringChoiceBox.getSelectionModel().select(2 + dayOfWeek.getValue());
        });
  }

  @FXML
  private void importServers() {
    try {
      final Map<DayOfWeek, Integer> absentOnDayOfWeekColumnIndices =
          importRegularAbsencesCheckBox.isSelected()
              ? absentOnDayOfWeekChoiceBoxes.entrySet().stream()
                  .collect(
                      Collectors.toUnmodifiableMap(
                          Entry::getKey,
                          absentOnDayOfWeekChoiceBox ->
                              absentOnDayOfWeekChoiceBox
                                  .getValue()
                                  .getSelectionModel()
                                  .getSelectedIndex()))
              : Map.of();
      final var readServers =
          PoiIO.readServers(
              input,
              surnameHeadingChoiceBox.getSelectionModel().getSelectedIndex(),
              forenameHeadingChoiceBox.getSelectionModel().getSelectedIndex(),
              yearHeadingChoiceBox.getSelectionModel().getSelectedIndex(),
              absentOnDayOfWeekColumnIndices);
      readServers.stream()
          .filter(readServer -> servers.putIfAbsent(readServer, readServer) != null)
          .forEach(
              readServer -> {
                final var currentServer = servers.get(readServer);
                if (importRegularAbsencesCheckBox.isSelected()) {
                  currentServer.setWeeklyAbsences(readServer.getWeeklyAbsences());
                }
              });
      serversConsumer.accept(List.copyOf(servers.values()));
      ((Stage) surnameHeadingChoiceBox.getScene().getWindow()).close();
    } catch (final IllegalStateException e) {
      LOGGER.debug("Unable to parse cells.", e);
      LOGGER.error("Unable to parse cells. Do the cell types match the required ones?");
    }
  }
}
