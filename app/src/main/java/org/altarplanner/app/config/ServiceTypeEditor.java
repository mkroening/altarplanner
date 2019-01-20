package org.altarplanner.app.config;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.xml.UnknownJAXBException;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ServiceTypeEditor {

    @FXML private Button removeButton;
    @FXML private TextField nameTextField;
    @FXML private TextField maxYearTextField;
    @FXML private TextField minYearTextField;
    @FXML private ListView<ServiceType> serviceTypeListView;

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
                applyChanges = true;
            }
        });

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                serviceTypeListView.getSelectionModel().getSelectedItem().setName(newValue);
                serviceTypeListView.getItems().sort(Comparator.naturalOrder());
            }
        });

        maxYearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    serviceTypeListView.getSelectionModel().getSelectedItem().setMaxYear(Integer.parseInt(newValue));
                    maxYearTextField.getStyleClass().remove("text-input-error");
                    serviceTypeListView.getItems().sort(Comparator.naturalOrder());
                } catch (NumberFormatException e) {
                    if (!maxYearTextField.getStyleClass().contains("text-input-error"))
                        maxYearTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        minYearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyChanges) {
                try {
                    serviceTypeListView.getSelectionModel().getSelectedItem().setMinYear(Integer.parseInt(newValue));
                    minYearTextField.getStyleClass().remove("text-input-error");
                    serviceTypeListView.getItems().sort(Comparator.naturalOrder());
                } catch (NumberFormatException e) {
                    if (!minYearTextField.getStyleClass().contains("text-input-error"))
                        minYearTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        serviceTypeListView.getItems().setAll(Launcher.CONFIG.getServiceTypes());
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

    @FXML private void saveAndBack() throws IOException, UnknownJAXBException {
        Launcher.CONFIG.setServiceTypes(List.copyOf(serviceTypeListView.getItems()));
        Launcher.CONFIG.save();
        Launcher.loadParent("launcher.fxml", true);
    }

    @FXML private void addServiceType() {
        ServiceType serviceType = new ServiceType();
        serviceTypeListView.getItems().add(serviceType);
        setDisable(false);
        serviceTypeListView.getSelectionModel().select(serviceType);
        serviceTypeListView.getItems().sort(Comparator.naturalOrder());
    }

    @FXML private void removeServiceType() {
        Launcher.CONFIG.remove(serviceTypeListView.getSelectionModel().getSelectedItem());
        serviceTypeListView.getItems().remove(serviceTypeListView.getSelectionModel().getSelectedItem());
        if (serviceTypeListView.getItems().isEmpty())
            setDisable(true);
    }

}
