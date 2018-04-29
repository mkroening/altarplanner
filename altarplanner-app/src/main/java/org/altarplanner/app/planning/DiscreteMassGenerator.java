package org.altarplanner.app.planning;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.DateSpan;
import org.altarplanner.core.domain.mass.DiscreteMass;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiscreteMassGenerator {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Config config;
    private List<Consumer<List<DiscreteMass>>> massesConsumers;

    @FXML private void initialize() {
        startDatePicker.setValue(LocalDate.now().plusMonths(1));
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    @SafeVarargs
    public final void initData(Config config, Consumer<List<DiscreteMass>>... consumers) {
        this.config = config;
        this.massesConsumers = List.of(consumers);
    }

    @FXML private void generateMasses() {
        List<DiscreteMass> masses = config.getDiscreteMassParallelStreamWithin(new DateSpan(startDatePicker.getValue(), endDatePicker.getValue())).collect(Collectors.toList());
        massesConsumers.forEach(listConsumer -> listConsumer.accept(masses));
        ((Stage)startDatePicker.getScene().getWindow()).close();
    }

}
