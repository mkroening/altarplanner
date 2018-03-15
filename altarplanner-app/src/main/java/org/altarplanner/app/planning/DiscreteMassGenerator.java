package org.altarplanner.app.planning;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import org.altarplanner.app.ConfigAware;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.DateSpan;
import org.altarplanner.core.domain.mass.DiscreteMass;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DiscreteMassGenerator implements ConfigAware {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Config config;

    @FXML private void initialize() {
        startDatePicker.setValue(LocalDate.now().plusMonths(1));
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    @Override
    public void initConfig(Config config) {
        this.config = config;
    }

    @FXML private void editMasses() {
        List<DiscreteMass> masses = config.getDiscreteMassParallelStreamWithin(new DateSpan(startDatePicker.getValue(), endDatePicker.getValue())).collect(Collectors.toList());
    }

}
