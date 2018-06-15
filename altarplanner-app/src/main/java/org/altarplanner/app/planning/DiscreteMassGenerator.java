package org.altarplanner.app.planning;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.util.LocalDateInterval;
import org.altarplanner.core.domain.mass.DiscreteMass;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiscreteMassGenerator {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Config config;
    private Consumer<List<DiscreteMass>> listConsumer;

    @FXML private void initialize() {
        startDatePicker.setValue(LocalDate.now().plusMonths(1));
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    public void initData(Config config, Consumer<List<DiscreteMass>> listConsumer) {
        this.config = config;
        this.listConsumer = listConsumer;
    }

    @FXML private void generateMasses() {
        listConsumer.accept(config.getDiscreteMassParallelStreamWithin(LocalDateInterval.of(startDatePicker.getValue(), endDatePicker.getValue())).collect(Collectors.toList()));
        ((Stage)startDatePicker.getScene().getWindow()).close();
    }

}
