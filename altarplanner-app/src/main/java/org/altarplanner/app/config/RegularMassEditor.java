package org.altarplanner.app.config;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.persistence.jaxb.JAXB;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.RegularMass;

import javax.xml.bind.JAXBException;

public class RegularMassEditor {

  @FXML private Button removeButton;
  @FXML private ListView<RegularMass> regularMassListView;
  @FXML private ChoiceBox<DayOfWeek> dayOfWeekChoiceBox;
  @FXML private TextField timeTextField;
  @FXML private TextField churchTextField;
  @FXML private TextField formTextField;
  @FXML private TextField annotationTextField;
  @FXML private TableView<ServiceType> serviceTypeCountTableView;
  @FXML private TableColumn<ServiceType, String> serviceTypeNameColumn;
  @FXML private TableColumn<ServiceType, String> serviceTypeCountColumn;

  private boolean applyChanges;

  @FXML
  private void initialize() {
    regularMassListView.setCellFactory(
        param ->
            new ListCell<>() {
              @Override
              protected void updateItem(RegularMass item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                  setText(null);
                  setGraphic(null);
                } else {
                  setText(
                      item.getDay().getDisplayName(TextStyle.FULL, Locale.getDefault())
                          + " - "
                          + item.getTime()
                              .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                          + " - "
                          + item.getChurch());
                }
              }
            });

    regularMassListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                applyChanges = false;
                dayOfWeekChoiceBox.getSelectionModel().select(newValue.getDay());
                timeTextField.setText(
                    newValue
                        .getTime()
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                churchTextField.setText(newValue.getChurch());
                formTextField.setText(newValue.getForm());
                annotationTextField.setText(newValue.getAnnotation());
                serviceTypeCountTableView.refresh();
                applyChanges = true;
              }
            });

    dayOfWeekChoiceBox.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(DayOfWeek object) {
            return object.getDisplayName(TextStyle.FULL, Locale.getDefault());
          }

          @Override
          public DayOfWeek fromString(String string) {
            return null;
          }
        });

    dayOfWeekChoiceBox.getItems().setAll(DayOfWeek.values());

    dayOfWeekChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                regularMassListView.getSelectionModel().getSelectedItem().setDay(newValue);
                regularMassListView.getItems().sort(Comparator.naturalOrder());
              }
            });

    timeTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                try {
                  regularMassListView
                      .getSelectionModel()
                      .getSelectedItem()
                      .setTime(
                          LocalTime.parse(
                              newValue, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                  timeTextField.getStyleClass().remove("text-input-error");
                  regularMassListView.getItems().sort(Comparator.naturalOrder());
                } catch (DateTimeParseException e) {
                  if (!timeTextField.getStyleClass().contains("text-input-error")) {
                    timeTextField.getStyleClass().add("text-input-error");
                  }
                }
              }
            });

    churchTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                regularMassListView.getSelectionModel().getSelectedItem().setChurch(newValue);
                regularMassListView.getItems().sort(Comparator.naturalOrder());
              }
            });

    formTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                regularMassListView.getSelectionModel().getSelectedItem().setForm(newValue);
              }
            });

    annotationTextField
        .textProperty()
        .addListener(
            ((observable, oldValue, newValue) -> {
              if (applyChanges) {
                regularMassListView
                    .getSelectionModel()
                    .getSelectedItem()
                    .setAnnotation(newValue.isBlank() ? null : newValue.trim());
              }
            }));

    serviceTypeNameColumn.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getDesc()));

    serviceTypeCountColumn.setCellFactory(
        param -> new TextFieldTableCell<>(new DefaultStringConverter()));

    serviceTypeCountColumn.setCellValueFactory(
        param -> {
          if (applyChanges) {
            return new SimpleStringProperty(
                String.valueOf(
                    regularMassListView
                        .getSelectionModel()
                        .getSelectedItem()
                        .getServiceTypeCounts()
                        .getOrDefault(param.getValue(), 0)));
          } else {
            return null;
          }
        });

    serviceTypeCountColumn.setOnEditCommit(
        event -> {
          if (applyChanges) {
            String newValue = event.getNewValue();
            if ("".equals(newValue) || "0".equals(newValue)) {
              regularMassListView
                  .getSelectionModel()
                  .getSelectedItem()
                  .getServiceTypeCounts()
                  .remove(event.getRowValue());
            } else {
              try {
                regularMassListView
                    .getSelectionModel()
                    .getSelectedItem()
                    .getServiceTypeCounts()
                    .put(event.getRowValue(), Integer.parseInt(newValue));
              } catch (NumberFormatException e) {
                serviceTypeCountTableView.refresh();
              }
            }
          }
        });

    regularMassListView.getItems().setAll(Launcher.CONFIG.getRegularMasses());
    serviceTypeCountTableView.getItems().setAll(Launcher.CONFIG.getServiceTypes());
    if (!regularMassListView.getItems().isEmpty()) {
      regularMassListView.getSelectionModel().selectFirst();
    } else {
      setDisable(true);
    }
  }

  private void setDisable(boolean disable) {
    applyChanges = false;
    removeButton.setDisable(disable);
    regularMassListView.setDisable(disable);
    dayOfWeekChoiceBox.setDisable(disable);
    timeTextField.setDisable(disable);
    churchTextField.setDisable(disable);
    formTextField.setDisable(disable);
    annotationTextField.setDisable(disable);
    serviceTypeCountTableView.setEditable(!disable);
    if (disable) {
      dayOfWeekChoiceBox.getSelectionModel().clearSelection();
      timeTextField.clear();
      churchTextField.clear();
      formTextField.clear();
      annotationTextField.clear();
      serviceTypeCountTableView.refresh();
    }
  }

  @FXML
  private void addRegularMass() {
    RegularMass regularMass = new RegularMass();
    regularMassListView.getItems().add(regularMass);
    setDisable(false);
    regularMassListView.getSelectionModel().select(regularMass);
    regularMassListView.getItems().sort(Comparator.naturalOrder());
  }

  @FXML
  private void removeRegularMass() {
    regularMassListView
        .getItems()
        .remove(regularMassListView.getSelectionModel().getSelectedItem());
    if (regularMassListView.getItems().isEmpty()) {
      setDisable(true);
    }
  }

  @FXML
  private void saveAndBack() throws IOException, JAXBException {
    Launcher.CONFIG.setRegularMasses(List.copyOf(regularMassListView.getItems()));
    JAXB.marshalConfig(Launcher.CONFIG, Launcher.CONFIG_PATH);
    Launcher.loadParent("launcher.fxml", true);
  }
}
