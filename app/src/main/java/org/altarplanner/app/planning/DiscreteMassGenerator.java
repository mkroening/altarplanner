package org.altarplanner.app.planning;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.util.LocalDateInterval;
import org.altarplanner.core.domain.mass.DiscreteMass;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiscreteMassGenerator {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Consumer<List<DiscreteMass>> listConsumer;

    @FXML private void initialize() {
        startDatePicker.setValue(LocalDate.now().plusMonths(1));
        endDatePicker.setValue(LocalDate.now().plusMonths(1));

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isAfter(endDatePicker.getValue()))
                endDatePicker.setValue(newValue);
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(startDatePicker.getValue()))
                startDatePicker.setValue(newValue);
        });
    }

    public void initData(Consumer<List<DiscreteMass>> listConsumer) {
        this.listConsumer = listConsumer;
    }

    @FXML private void generateMasses() {
        listConsumer.accept(Launcher.CONFIG
                .getDiscreteMassParallelStreamWithin(LocalDateInterval.of(startDatePicker.getValue(), endDatePicker.getValue()))
                .collect(Collectors.toUnmodifiableList()));
        ((Stage)startDatePicker.getScene().getWindow()).close();
    }

}
