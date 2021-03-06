package org.altarplanner.app.planning;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.planning.domain.mass.PlanningMassTemplate;
import org.threeten.extra.LocalDateRange;

public class PlanningMassTemplateGenerator {

  @FXML private DatePicker startDatePicker;
  @FXML private DatePicker endDatePicker;

  private Consumer<List<PlanningMassTemplate>> listConsumer;

  @FXML
  private void initialize() {
    startDatePicker.setValue(LocalDate.now().plusMonths(1));
    endDatePicker.setValue(LocalDate.now().plusMonths(1));

    startDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.isAfter(endDatePicker.getValue())) {
                endDatePicker.setValue(newValue);
              }
            });

    endDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.isBefore(startDatePicker.getValue())) {
                startDatePicker.setValue(newValue);
              }
            });
  }

  public void initData(Consumer<List<PlanningMassTemplate>> listConsumer) {
    this.listConsumer = listConsumer;
  }

  @FXML
  private void generateMasses() {
    listConsumer.accept(
        Launcher.CONFIG
            .getPlanningMassTemplateStreamFromRegularMassesIn(
                LocalDateRange.ofClosed(startDatePicker.getValue(), endDatePicker.getValue()))
            .collect(Collectors.toUnmodifiableList()));
    ((Stage) startDatePicker.getScene().getWindow()).close();
  }
}
