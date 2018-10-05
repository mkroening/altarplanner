package org.altarplanner.app.planning;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DefaultStringConverter;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.mass.DatedDraftMass;
import org.altarplanner.core.util.LocalDateRangeUtil;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnexpectedElementException;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.altarplanner.core.domain.DiscreteMassCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Comparator;

public class DiscreteMassEditor {

    @FXML private Button removeButton;
    @FXML private ListView<DatedDraftMass> discreteMassListView;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeTextField;
    @FXML private TextField churchTextField;
    @FXML private TextField formTextField;
    @FXML private TextField annotationTextField;
    @FXML private TableView<ServiceType> serviceTypeCountTableView;
    @FXML private TableColumn<ServiceType, String> serviceTypeNameColumn;
    @FXML private TableColumn<ServiceType, String> serviceTypeCountColumn;

    private boolean applyChanges;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscreteMassEditor.class);

    @FXML private void initialize() {
        discreteMassListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DatedDraftMass item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) + " - " +
                            item.getChurch());
                }
            }
        });

        discreteMassListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyChanges = false;
                datePicker.setValue(newValue.getDateTime().toLocalDate());
                timeTextField.setText(newValue.getDateTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                churchTextField.setText(newValue.getChurch());
                formTextField.setText(newValue.getForm());
                annotationTextField.setText(newValue.getAnnotation());
                serviceTypeCountTableView.refresh();
                applyChanges = true;
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                final var time = discreteMassListView.getSelectionModel().getSelectedItem().getDateTime().toLocalTime();
                discreteMassListView.getSelectionModel().getSelectedItem().setDateTime(LocalDateTime.of(newValue, time));
                discreteMassListView.getItems().sort(Comparator.naturalOrder());
            }
        });

        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    final var date = discreteMassListView.getSelectionModel().getSelectedItem().getDateTime().toLocalDate();
                    final var newTime = LocalTime.parse(newValue, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
                    discreteMassListView.getSelectionModel().getSelectedItem().setDateTime(LocalDateTime.of(date, newTime));
                    timeTextField.getStyleClass().remove("text-input-error");
                    discreteMassListView.getItems().sort(Comparator.naturalOrder());
                } catch (DateTimeParseException e) {
                    if (!timeTextField.getStyleClass().contains("text-input-error"))
                        timeTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        churchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                discreteMassListView.getSelectionModel().getSelectedItem().setChurch(newValue);
                discreteMassListView.getItems().sort(Comparator.naturalOrder());
            }
        });

        formTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                discreteMassListView.getSelectionModel().getSelectedItem().setForm(newValue);
            }
        });

        annotationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                discreteMassListView.getSelectionModel().getSelectedItem().setForm(newValue.isBlank() ? null : newValue.trim());
            }
        });

        serviceTypeNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDesc()));

        serviceTypeCountColumn.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));

        serviceTypeCountColumn.setCellValueFactory(param -> {
            if (applyChanges)
                return new SimpleStringProperty(String.valueOf(discreteMassListView.getSelectionModel().getSelectedItem().getServiceTypeCounts().getOrDefault(param.getValue(), 0)));
            else
                return null;
        });

        serviceTypeCountColumn.setOnEditCommit(event -> {
            if (applyChanges) {
                String newValue = event.getNewValue();
                if ("".equals(newValue) || "0".equals(newValue)) {
                    discreteMassListView.getSelectionModel().getSelectedItem().getServiceTypeCounts().remove(event.getRowValue());
                } else try {
                    discreteMassListView.getSelectionModel().getSelectedItem().getServiceTypeCounts().put(event.getRowValue(), Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    serviceTypeCountTableView.refresh();
                }
            }
        });

        serviceTypeCountTableView.getItems().setAll(Launcher.CONFIG.getServiceTypes());
        if (!discreteMassListView.getItems().isEmpty())
            discreteMassListView.getSelectionModel().selectFirst();
        else
            setDisable(true);
    }

    private void setDisable(boolean disable) {
        applyChanges = false;
        removeButton.setDisable(disable);
        discreteMassListView.setDisable(disable);
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

    @FXML private void addDiscreteMass() {
        DatedDraftMass discreteMass = new DatedDraftMass();
        discreteMassListView.getItems().add(discreteMass);
        setDisable(false);
        discreteMassListView.getSelectionModel().select(discreteMass);
        discreteMassListView.getItems().sort(Comparator.naturalOrder());
    }

    @FXML private void removeDiscreteMass() {
        discreteMassListView.getItems().remove(discreteMassListView.getSelectionModel().getSelectedItem());
        if (discreteMassListView.getItems().isEmpty())
            setDisable(true);
    }

    @FXML private void generateFromRegularMasses() throws IOException {
        Launcher.loadParent("planning/discreteMassGenerator.fxml", false,
                discreteMassGenerator -> ((DiscreteMassGenerator)discreteMassGenerator)
                        .initData(discreteMasses -> {
                            discreteMassListView.getItems().addAll(discreteMasses);
                            setDisable(false);
                            if (discreteMassListView.getSelectionModel().getSelectedItem() == null)
                                discreteMassListView.getSelectionModel().selectFirst();
                            discreteMassListView.getItems().sort(Comparator.naturalOrder());
                        }));
    }

    @FXML private void openFile() throws IOException, UnknownJAXBException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.openDiscreteMasses"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("masses/");
        Files.createDirectories(directory.toPath());
        fileChooser.setInitialDirectory(directory);

        File selectedFile = fileChooser.showOpenDialog(removeButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                DiscreteMassCollection massCollection = JaxbIO.unmarshal(selectedFile, DiscreteMassCollection.class);
                serviceTypeCountTableView.getItems().setAll(massCollection.getServiceTypes());
                discreteMassListView.getItems().setAll(massCollection.getDatedDraftMasses());
                LOGGER.info("Masses have been loaded from {}", selectedFile);

                setDisable(false);
                discreteMassListView.getSelectionModel().selectFirst();
                discreteMassListView.getItems().sort(Comparator.naturalOrder());
            } catch (UnexpectedElementException e) {
                LOGGER.error("No masses could have been loaded");
            }
        } else LOGGER.info("No masses have been loaded, because no file has been selected");
    }

    @FXML private void saveAsAndBack() throws IOException, UnknownJAXBException {
        if (!discreteMassListView.getItems().isEmpty()) {
            final DiscreteMassCollection massCollection = new DiscreteMassCollection(discreteMassListView.getItems());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveDiscreteMasses"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
            File directory = new File("masses/");
            Files.createDirectories(directory.toPath());
            fileChooser.setInitialDirectory(directory);
            fileChooser.setInitialFileName(LocalDateRangeUtil.getHyphenString(massCollection.getDateRange()) + ".xml");

            File selectedFile = fileChooser.showSaveDialog(removeButton.getScene().getWindow());
            if (selectedFile != null) {
                JaxbIO.marshal(massCollection, selectedFile);
                LOGGER.info("Masses have been saved as {}", selectedFile);

                Launcher.loadParent("launcher.fxml", true);
            } else LOGGER.info("Masses have not been saved, because no file has been selected");
        } else {
            LOGGER.info("No Masses available to save");
            Launcher.loadParent("launcher.fxml", true);
        }
    }

}
