package org.altarplanner.app.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import org.altarplanner.app.ConfigAware;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.mass.RegularMass;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class RegularMassEditor implements ConfigAware {

    @FXML private Button removeButton;
    @FXML private ListView<RegularMass> regularMassListView;
    @FXML private ChoiceBox<DayOfWeek> dayOfWeekChoiceBox;
    @FXML private TextField timeTextField;
    @FXML private TextField churchTextField;
    @FXML private TextField formTextField;
    @FXML private TableView<ServiceType> serviceTypeCountTableView;
    @FXML private TableColumn<ServiceType, String> serviceTypeNameColumn;
    @FXML private TableColumn<ServiceType, String> serviceTypeCountColumn;

    private Config config;
    private RegularMass selectedRegularMass;
    private boolean applyChanges;

    @FXML private void initialize() {
        regularMassListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RegularMass item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });

        regularMassListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyChanges = false;
                dayOfWeekChoiceBox.getSelectionModel().select(newValue.getDay());
                timeTextField.setText(newValue.getTime().toString());
                churchTextField.setText(newValue.getChurch());
                formTextField.setText(newValue.getForm());
                selectedRegularMass = newValue;
                serviceTypeCountTableView.refresh();
                applyChanges = true;
            }
        });

        dayOfWeekChoiceBox.getItems().setAll(DayOfWeek.values());

        dayOfWeekChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                 selectedRegularMass.setDay(newValue);
                 regularMassListView.getItems().sort(RegularMass.getNaturalOrderComparator());
            }
        });

        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    selectedRegularMass.setTime(LocalTime.parse(newValue));
                    timeTextField.getStyleClass().remove("text-input-error");
                    regularMassListView.getItems().sort(RegularMass.getNaturalOrderComparator());
                } catch (DateTimeParseException e) {
                    if (!timeTextField.getStyleClass().contains("text-input-error"))
                        timeTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        churchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                selectedRegularMass.setChurch(newValue);
                regularMassListView.getItems().sort(RegularMass.getNaturalOrderComparator());
            }
        });

        formTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                selectedRegularMass.setForm(newValue);
            }
        });

        serviceTypeNameColumn.prefWidthProperty().bind(
                serviceTypeCountTableView.widthProperty()
                .subtract(62));

        serviceTypeNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDesc()));

        serviceTypeCountColumn.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));

        serviceTypeCountColumn.setCellValueFactory(param -> {
            if (applyChanges)
                return new SimpleStringProperty(String.valueOf(selectedRegularMass.getServiceTypeCount().getOrDefault(param.getValue(), 0)));
            else
                return null;
        });

        serviceTypeCountColumn.setOnEditCommit(event -> {
            if (applyChanges) {
                String newValue = event.getNewValue();
                if ("".equals(newValue) || "0".equals(newValue)) {
                    selectedRegularMass.getServiceTypeCount().remove(event.getRowValue());
                } else try {
                    selectedRegularMass.getServiceTypeCount().put(event.getRowValue(), Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    serviceTypeCountTableView.refresh();
                }
            }
        });

    }

    @Override
    public void initConfig(Config config) {
        this.config = config;
        regularMassListView.getItems().setAll(config.getRegularMasses());
        serviceTypeCountTableView.getItems().setAll(config.getServiceTypes());
        if (!regularMassListView.getItems().isEmpty())
            regularMassListView.getSelectionModel().selectFirst();
        else
            setDisable(true);
    }

    private void setDisable(boolean disable) {
        applyChanges = false;
        removeButton.setDisable(disable);
        regularMassListView.setDisable(disable);
        dayOfWeekChoiceBox.setDisable(disable);
        timeTextField.setDisable(disable);
        churchTextField.setDisable(disable);
        formTextField.setDisable(disable);
        serviceTypeCountTableView.setEditable(!disable);
        if (disable) {
            dayOfWeekChoiceBox.getSelectionModel().clearSelection();
            timeTextField.clear();
            churchTextField.clear();
            formTextField.clear();
            serviceTypeCountTableView.refresh();
        }
    }

    @FXML private void addRegularMass() {
        RegularMass regularMass = new RegularMass();
        regularMassListView.getItems().add(regularMass);
        setDisable(false);
        regularMassListView.getSelectionModel().select(regularMass);
        regularMassListView.getItems().sort(RegularMass.getNaturalOrderComparator());
    }

    @FXML private void removeRegularMass() {
        regularMassListView.getItems().remove(selectedRegularMass);
        if (regularMassListView.getItems().isEmpty())
            setDisable(true);
    }

    @FXML private void loadLauncher() throws IOException {
        config.setRegularMasses(regularMassListView.getItems().parallelStream().collect(Collectors.toList()));
        config.save();
        Launcher.loadParent("launcher.fxml", config);
    }

}
