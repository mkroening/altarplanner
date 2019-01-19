package org.altarplanner.app.planning;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Consumer;

public class FeastDayEditor {

    @FXML private Button removeButton;
    @FXML private DatePicker datePicker;
    @FXML private ListView<LocalDate> feastDayListView;

    private boolean applyDatePickerChanges;
    private Consumer<Collection<LocalDate>> feastDaysConsumer;

    @FXML private void initialize() {
        removeButton.disableProperty().bind(feastDayListView.getSelectionModel().selectedItemProperty().isNull());
        datePicker.disableProperty().bind(feastDayListView.getSelectionModel().selectedItemProperty().isNull());

        feastDayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyDatePickerChanges = false;
                datePicker.setValue(newValue);
                applyDatePickerChanges = true;
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyDatePickerChanges) {
                feastDayListView.getItems().remove(feastDayListView.getSelectionModel().getSelectedIndex());
                addAndSelect(newValue);
            }
        });
    }

    private void addAndSelect(LocalDate feastDay) {
        feastDayListView.getItems().add(feastDay);
        feastDayListView.getSelectionModel().select(feastDay);
        feastDayListView.getItems().sort(LocalDate::compareTo);
    }

    void addFeastDays(Collection<LocalDate> feastDays) {
        feastDayListView.getItems().addAll(feastDays);
        feastDayListView.getItems().sort(LocalDate::compareTo);
        feastDayListView.getSelectionModel().selectFirst();
    }

    void setFeastDaysConsumer(Consumer<Collection<LocalDate>> feastDaysConsumer) {
        this.feastDaysConsumer = feastDaysConsumer;
    }

    @FXML private void addFeastDay() {
        addAndSelect(LocalDate.now().plusMonths(1));
    }

    @FXML private void removeFeastDay() {
        feastDayListView.getItems().remove(feastDayListView.getSelectionModel().getSelectedIndex());
    }

    @FXML private void applyAndClose() {
        feastDaysConsumer.accept(feastDayListView.getItems());
        ((Stage)removeButton.getScene().getWindow()).close();
    }
}
