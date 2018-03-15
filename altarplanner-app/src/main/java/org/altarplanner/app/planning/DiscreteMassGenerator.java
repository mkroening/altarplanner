package org.altarplanner.app.planning;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.DateSpan;
import org.altarplanner.core.domain.mass.DiscreteMass;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DiscreteMassGenerator {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Config config;

    @FXML private void initialize() {
        startDatePicker.setValue(LocalDate.now().plusMonths(1));
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    public void initData(Config config) {
        this.config = config;
    }

    @FXML private void editMasses() throws IOException {
        List<DiscreteMass> masses = config.getDiscreteMassParallelStreamWithin(new DateSpan(startDatePicker.getValue(), endDatePicker.getValue())).collect(Collectors.toList());
        Launcher.loadParent("planning/discreteMassEditor.fxml", discreteMassEditor -> ((DiscreteMassEditor)discreteMassEditor).initData(config, masses));
    }

}
