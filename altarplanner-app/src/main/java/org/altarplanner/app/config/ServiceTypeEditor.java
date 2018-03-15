package org.altarplanner.app.config;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.altarplanner.app.ConfigAware;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.ServiceType;

import java.io.IOException;
import java.util.stream.Collectors;

public class ServiceTypeEditor implements ConfigAware {

    @FXML private Button removeButton;
    @FXML private TextField nameTextField;
    @FXML private TextField maxYearTextField;
    @FXML private TextField minYearTextField;
    @FXML private ListView<ServiceType> serviceTypeListView;

    private Config config;
    private ServiceType selectedServiceType;
    private boolean applyChanges;

    @FXML private void initialize() {
        serviceTypeListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ServiceType item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });

        serviceTypeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyChanges = false;
                nameTextField.setText(newValue.getName());
                maxYearTextField.setText(String.valueOf(newValue.getMaxYear()));
                minYearTextField.setText(String.valueOf(newValue.getMinYear()));
                selectedServiceType = newValue;
                applyChanges = true;
            }
        });

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                selectedServiceType.setName(newValue);
                serviceTypeListView.getItems().sort(ServiceType.getNaturalOrderComparator());
            }
        });

        maxYearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    selectedServiceType.setMaxYear(Integer.parseInt(newValue));
                    maxYearTextField.getStyleClass().remove("text-input-error");
                    serviceTypeListView.getItems().sort(ServiceType.getNaturalOrderComparator());
                } catch (NumberFormatException e) {
                    if (!maxYearTextField.getStyleClass().contains("text-input-error"))
                        maxYearTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        minYearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    selectedServiceType.setMinYear(Integer.parseInt(newValue));
                    minYearTextField.getStyleClass().remove("text-input-error");
                    serviceTypeListView.getItems().sort(ServiceType.getNaturalOrderComparator());
                } catch (NumberFormatException e) {
                    if (!minYearTextField.getStyleClass().contains("text-input-error"))
                        minYearTextField.getStyleClass().add("text-input-error");
                }
            }
        });

    }

    @Override
    public void initConfig(Config config) {
        this.config = config;
        serviceTypeListView.getItems().setAll(config.getServiceTypes());
        if (!serviceTypeListView.getItems().isEmpty())
            serviceTypeListView.getSelectionModel().selectFirst();
        else
            setDisable(true);
    }

    private void setDisable(boolean disable) {
        applyChanges = false;
        removeButton.setDisable(disable);
        serviceTypeListView.setDisable(disable);
        nameTextField.setDisable(disable);
        maxYearTextField.setDisable(disable);
        minYearTextField.setDisable(disable);
        if (disable) {
            nameTextField.clear();
            maxYearTextField.clear();
            minYearTextField.clear();
        }
    }

    @FXML private void loadLauncher() throws IOException {
        config.setServiceTypes(serviceTypeListView.getItems().parallelStream().collect(Collectors.toList()));
        config.save();
        Launcher.loadParent("launcher.fxml", launcher -> ((Launcher)launcher).initConfig(config));
    }

    @FXML private void addServiceType() {
        ServiceType serviceType = new ServiceType();
        serviceTypeListView.getItems().add(serviceType);
        setDisable(false);
        serviceTypeListView.getSelectionModel().select(serviceType);
        serviceTypeListView.getItems().sort(ServiceType.getNaturalOrderComparator());
    }

    @FXML private void removeServiceType() {
        config.removeFromRegularMasses(selectedServiceType);
        serviceTypeListView.getItems().remove(selectedServiceType);
        if (serviceTypeListView.getItems().isEmpty())
            setDisable(true);
    }

}
