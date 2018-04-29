package org.altarplanner.app.planning;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DefaultStringConverter;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.io.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;

public class DiscreteMassEditor {

    @FXML private Button removeButton;
    @FXML private ListView<DiscreteMass> discreteMassListView;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeTextField;
    @FXML private TextField churchTextField;
    @FXML private TextField formTextField;
    @FXML private TableView<ServiceType> serviceTypeCountTableView;
    @FXML private TableColumn<ServiceType, String> serviceTypeNameColumn;
    @FXML private TableColumn<ServiceType, String> serviceTypeCountColumn;

    private Config config;
    private DiscreteMass selectedDiscreteMass;
    private boolean applyChanges;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscreteMassEditor.class);

    @FXML private void initialize() {
        discreteMassListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DiscreteMass item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });

        discreteMassListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyChanges = false;
                datePicker.setValue(newValue.getDate());
                timeTextField.setText(newValue.getTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                churchTextField.setText(newValue.getChurch());
                formTextField.setText(newValue.getForm());
                selectedDiscreteMass = newValue;
                serviceTypeCountTableView.refresh();
                applyChanges = true;
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                selectedDiscreteMass.setDate(newValue);
                discreteMassListView.getItems().sort(DiscreteMass.getDescComparator());
            }
        });

        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    selectedDiscreteMass.setTime(LocalTime.parse(newValue, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                    timeTextField.getStyleClass().remove("text-input-error");
                    discreteMassListView.getItems().sort(DiscreteMass.getDescComparator());
                } catch (DateTimeParseException e) {
                    if (!timeTextField.getStyleClass().contains("text-input-error"))
                        timeTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        churchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                selectedDiscreteMass.setChurch(newValue);
                discreteMassListView.getItems().sort(DiscreteMass.getDescComparator());
            }
        });

        formTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                selectedDiscreteMass.setForm(newValue);
            }
        });

        serviceTypeNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDesc()));

        serviceTypeCountColumn.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));

        serviceTypeCountColumn.setCellValueFactory(param -> {
            if (applyChanges)
                return new SimpleStringProperty(String.valueOf(selectedDiscreteMass.getServiceTypeCount().getOrDefault(param.getValue(), 0)));
            else
                return null;
        });

        serviceTypeCountColumn.setOnEditCommit(event -> {
            if (applyChanges) {
                String newValue = event.getNewValue();
                if ("".equals(newValue) || "0".equals(newValue)) {
                    selectedDiscreteMass.getServiceTypeCount().remove(event.getRowValue());
                } else try {
                    selectedDiscreteMass.getServiceTypeCount().put(event.getRowValue(), Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    serviceTypeCountTableView.refresh();
                }
            }
        });
    }

    public void initData(Config config) {
        this.config = config;
        serviceTypeCountTableView.getItems().setAll(config.getServiceTypes());
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
        serviceTypeCountTableView.setEditable(!disable);
        if (disable) {
            datePicker.setValue(null);
            timeTextField.clear();
            churchTextField.clear();
            formTextField.clear();
            serviceTypeCountTableView.refresh();
        }
    }

    @FXML private void addDiscreteMass() {
        DiscreteMass discreteMass = new DiscreteMass();
        discreteMassListView.getItems().add(discreteMass);
        setDisable(false);
        discreteMassListView.getSelectionModel().select(discreteMass);
        discreteMassListView.getItems().sort(DiscreteMass.getDescComparator());
    }

    @FXML private void removeDiscreteMass() {
        discreteMassListView.getItems().remove(selectedDiscreteMass);
        if (discreteMassListView.getItems().isEmpty())
            setDisable(true);
    }

    @FXML private void generateFromRegularMasses() throws IOException {
        Launcher.loadParent("planning/discreteMassGenerator.fxml", false,
                discreteMassGenerator -> ((DiscreteMassGenerator)discreteMassGenerator)
                        .initData(config, discreteMasses -> {
                            discreteMassListView.getItems().addAll(discreteMasses);
                            setDisable(false);
                            if (selectedDiscreteMass == null)
                                discreteMassListView.getSelectionModel().selectFirst();
                            discreteMassListView.getItems().sort(DiscreteMass.getDescComparator());
                        }));
    }

    @FXML private void loadFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("openDiscreteMasses"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("masses/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);

        File selectedFile = fileChooser.showOpenDialog(removeButton.getScene().getWindow());

        List<DiscreteMass> masses = XML.readList(selectedFile, DiscreteMass.class);
        discreteMassListView.getItems().setAll(masses);
        LOGGER.info("Masses have been loaded from {}", selectedFile);

        setDisable(false);
        discreteMassListView.getSelectionModel().selectFirst();
        discreteMassListView.getItems().sort(DiscreteMass.getDescComparator());
    }

    @FXML private void saveAsAndExit() throws IOException {
        if (!discreteMassListView.getItems().isEmpty()) {
            List<DiscreteMass> masses = List.copyOf(discreteMassListView.getItems());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("saveDiscreteMasses"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
            File directory = new File("masses/");
            directory.mkdirs();
            fileChooser.setInitialDirectory(directory);
            fileChooser.setInitialFileName(masses.get(0).getDate() + "_" + masses.get(masses.size() - 1).getDate() + ".xml");

            File selectedFile = fileChooser.showSaveDialog(removeButton.getScene().getWindow());
            XML.writeList(masses, DiscreteMass.class, selectedFile);
            LOGGER.info("Masses have been saved as {}", selectedFile);

            Launcher.loadParent("launcher.fxml", true);
        } else LOGGER.info("No Masses available to save");
    }

}
