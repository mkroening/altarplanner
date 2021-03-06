package org.altarplanner.app.planning;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DefaultStringConverter;
import javax.xml.bind.JAXBException;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.persistence.jaxb.JAXB;
import org.altarplanner.core.planning.domain.ServiceType;
import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.planning.domain.state.ScheduleTemplate;
import org.altarplanner.core.planning.util.LocalDateRangeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleTemplateEditor {

  @FXML private Button removeButton;
  @FXML private ListView<PlanningMassTemplate> planningMassTemplateListView;
  @FXML private DatePicker datePicker;
  @FXML private TextField timeTextField;
  @FXML private TextField churchTextField;
  @FXML private TextField formTextField;
  @FXML private TextField annotationTextField;
  @FXML private TableView<ServiceType> serviceTypeCountTableView;
  @FXML private TableColumn<ServiceType, String> serviceTypeNameColumn;
  @FXML private TableColumn<ServiceType, String> serviceTypeCountColumn;

  private List<LocalDate> feastDays = List.of();
  private boolean applyChanges;

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTemplateEditor.class);

  @FXML
  private void initialize() {
    planningMassTemplateListView.setCellFactory(
        param ->
            new ListCell<>() {
              @Override
              protected void updateItem(PlanningMassTemplate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                  setText(null);
                  setGraphic(null);
                } else {
                  setText(
                      item.getDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                          + " - "
                          + item.getChurch());
                }
              }
            });

    planningMassTemplateListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                applyChanges = false;
                datePicker.setValue(newValue.getDateTime().toLocalDate());
                timeTextField.setText(
                    newValue
                        .getDateTime()
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                churchTextField.setText(newValue.getChurch());
                formTextField.setText(newValue.getForm());
                annotationTextField.setText(newValue.getAnnotation());
                serviceTypeCountTableView.refresh();
                applyChanges = true;
              }
            });

    datePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                final var time =
                    planningMassTemplateListView
                        .getSelectionModel()
                        .getSelectedItem()
                        .getDateTime()
                        .toLocalTime();
                planningMassTemplateListView
                    .getSelectionModel()
                    .getSelectedItem()
                    .setDateTime(LocalDateTime.of(newValue, time));
                planningMassTemplateListView.getItems().sort(Comparator.naturalOrder());
              }
            });

    timeTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                try {
                  final var date =
                      planningMassTemplateListView
                          .getSelectionModel()
                          .getSelectedItem()
                          .getDateTime()
                          .toLocalDate();
                  final var newTime =
                      LocalTime.parse(
                          newValue, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
                  planningMassTemplateListView
                      .getSelectionModel()
                      .getSelectedItem()
                      .setDateTime(LocalDateTime.of(date, newTime));
                  timeTextField.getStyleClass().remove("text-input-error");
                  planningMassTemplateListView.getItems().sort(Comparator.naturalOrder());
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
                planningMassTemplateListView
                    .getSelectionModel()
                    .getSelectedItem()
                    .setChurch(newValue);
                planningMassTemplateListView.getItems().sort(Comparator.naturalOrder());
              }
            });

    formTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                planningMassTemplateListView
                    .getSelectionModel()
                    .getSelectedItem()
                    .setForm(newValue);
              }
            });

    annotationTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyChanges) {
                planningMassTemplateListView
                    .getSelectionModel()
                    .getSelectedItem()
                    .setAnnotation(newValue.isBlank() ? null : newValue.trim());
              }
            });

    serviceTypeNameColumn.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getDesc()));

    serviceTypeCountColumn.setCellFactory(
        param -> new TextFieldTableCell<>(new DefaultStringConverter()));

    serviceTypeCountColumn.setCellValueFactory(
        param -> {
          if (applyChanges) {
            return new SimpleStringProperty(
                String.valueOf(
                    planningMassTemplateListView
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
              planningMassTemplateListView
                  .getSelectionModel()
                  .getSelectedItem()
                  .getServiceTypeCounts()
                  .remove(event.getRowValue());
            } else {
              try {
                planningMassTemplateListView
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

    serviceTypeCountTableView.getItems().setAll(Launcher.CONFIG.getServiceTypes());
    if (!planningMassTemplateListView.getItems().isEmpty()) {
      planningMassTemplateListView.getSelectionModel().selectFirst();
    } else {
      setDisable(true);
    }
  }

  private void setDisable(boolean disable) {
    applyChanges = false;
    removeButton.setDisable(disable);
    planningMassTemplateListView.setDisable(disable);
    datePicker.setDisable(disable);
    timeTextField.setDisable(disable);
    churchTextField.setDisable(disable);
    formTextField.setDisable(disable);
    annotationTextField.setDisable(disable);
    serviceTypeCountTableView.setEditable(!disable);
    if (disable) {
      datePicker.setValue(null);
      timeTextField.clear();
      churchTextField.clear();
      formTextField.clear();
      annotationTextField.clear();
      serviceTypeCountTableView.refresh();
    }
  }

  @FXML
  private void addPlanningMassTemplate() {
    PlanningMassTemplate planningMassTemplate = new PlanningMassTemplate();
    planningMassTemplateListView.getItems().add(planningMassTemplate);
    setDisable(false);
    planningMassTemplateListView.getSelectionModel().select(planningMassTemplate);
    planningMassTemplateListView.getItems().sort(Comparator.naturalOrder());
  }

  @FXML
  private void removePlanningMassTemplate() {
    planningMassTemplateListView
        .getItems()
        .remove(planningMassTemplateListView.getSelectionModel().getSelectedItem());
    if (planningMassTemplateListView.getItems().isEmpty()) {
      setDisable(true);
    }
  }

  @FXML
  private void generateFromRegularMasses() throws IOException {
    Launcher.loadParent(
        "planning/planningMassTemplateGenerator.fxml",
        false,
        planningMassTemplateGenerator ->
            ((PlanningMassTemplateGenerator) planningMassTemplateGenerator)
                .initData(
                    planningMassTemplates -> {
                      planningMassTemplateListView.getItems().addAll(planningMassTemplates);
                      setDisable(false);
                      if (planningMassTemplateListView.getSelectionModel().getSelectedItem()
                          == null) {
                        planningMassTemplateListView.getSelectionModel().selectFirst();
                      }
                      planningMassTemplateListView.getItems().sort(Comparator.naturalOrder());
                    }));
  }

  @FXML
  private void openFile() throws IOException, JAXBException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(
        Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.openScheduleTemplate"));
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
    File directory = new File("masses/");
    Files.createDirectories(directory.toPath());
    fileChooser.setInitialDirectory(directory);

    File selectedFile = fileChooser.showOpenDialog(removeButton.getScene().getWindow());
    if (selectedFile != null) {
      ScheduleTemplate scheduleTemplate = JAXB.unmarshalScheduleTemplate(selectedFile.toPath());
      serviceTypeCountTableView.getItems().setAll(scheduleTemplate.getServiceTypes());
      planningMassTemplateListView.getItems().setAll(scheduleTemplate.getPlanningMassTemplates());
      feastDays = scheduleTemplate.getFeastDays();
      LOGGER.info("Masses have been loaded from {}", selectedFile);

      setDisable(false);
      planningMassTemplateListView.getSelectionModel().selectFirst();
      planningMassTemplateListView.getItems().sort(Comparator.naturalOrder());
    } else {
      LOGGER.info("No masses have been loaded, because no file has been selected");
    }
  }

  @FXML
  private void saveAsAndBack() throws IOException, JAXBException {
    if (!planningMassTemplateListView.getItems().isEmpty()) {
      final var scheduleTemplate =
          new ScheduleTemplate(planningMassTemplateListView.getItems(), feastDays);

      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(
          Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveScheduleTemplate"));
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
      File directory = new File("masses/");
      Files.createDirectories(directory.toPath());
      fileChooser.setInitialDirectory(directory);
      fileChooser.setInitialFileName(
          Launcher.RESOURCE_BUNDLE.getString("general.domain.scheduleTemplate")
              + '_'
              + LocalDateRangeUtil.getHyphenString(scheduleTemplate.getDateRange())
              + ".xml");

      File selectedFile = fileChooser.showSaveDialog(removeButton.getScene().getWindow());
      if (selectedFile != null) {
        JAXB.marshalScheduleTemplate(scheduleTemplate, selectedFile.toPath());
        LOGGER.info("Masses have been saved as {}", selectedFile);

        Launcher.loadParent("launcher.fxml", true);
      } else {
        LOGGER.info("Masses have not been saved, because no file has been selected");
      }
    } else {
      LOGGER.info("No Masses available to save");
      Launcher.loadParent("launcher.fxml", true);
    }
  }

  @FXML
  private void editFeastDays() throws IOException {
    Launcher.loadParent(
        "planning/feastDayEditor.fxml",
        false,
        feastDayEditor -> {
          ((FeastDayEditor) feastDayEditor).addFeastDays(feastDays);
          ((FeastDayEditor) feastDayEditor)
              .setFeastDaysConsumer(feastDays -> this.feastDays = List.copyOf(feastDays));
        });
  }
}
