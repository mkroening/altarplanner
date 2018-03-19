package org.altarplanner.app.planning;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.mass.GenericMass;
import org.altarplanner.core.solver.ScheduleSolver;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolverView {

    @FXML private TableColumn<Server, String> serverColumn;
    @FXML private TableView<Server> tableView;
    @FXML private Label scoreLabel;

    private Schedule schedule;

    @FXML
    private void initialize() {
        serverColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDesc()));
    }

    public void initData(Schedule schedule) {
        this.schedule = schedule;

        tableView.getItems().addAll(schedule.getServers());

        refreshColumns();

        new Thread(this::plan).start();
    }

    private void refreshColumns() {
        List<TableColumn<Server, ?>> newColumns = schedule.getDateMassesMap().entrySet().parallelStream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(localDateListEntry -> {
                    TableColumn<Server, String> dateColumn = new TableColumn<>(localDateListEntry.getKey().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    boolean disable = !schedule.getPlanningWindow().contains(localDateListEntry.getKey());

                    if (disable)
                        dateColumn.getStyleClass().add("table-column-disable");

                    dateColumn.getColumns().addAll(
                            localDateListEntry.getValue().parallelStream().sorted(GenericMass.getGenericDescComparator())
                                    .map(planningMass -> {
                                        TableColumn<Server, String> massColumn = new TableColumn<>(planningMass.getGenericDesc());
                                        massColumn.setCellValueFactory(param -> new SimpleStringProperty(planningMass.serviceDescOf(param.getValue())));
                                        if (disable)
                                            massColumn.getStyleClass().addAll("table-column-disable", "table-column-child-disable");
                                        return massColumn;
                                    }).collect(Collectors.toList()));

                    return dateColumn;
                }).collect(Collectors.toList());

        newColumns.add(0, tableView.getColumns().get(0));
        tableView.getColumns().setAll(newColumns);
    }

    private void plan() {
        ScheduleSolver scheduleSolver = new ScheduleSolver();
        scheduleSolver.addNewBestUiScoreStringConsumer(s -> Platform.runLater(() -> scoreLabel.setText(s)));
        scheduleSolver.addNewBestScheduleConsumer(schedule -> {
            this.schedule = schedule;
            Platform.runLater(this::refreshColumns);
        });
        schedule = scheduleSolver.solve(schedule);
        Platform.runLater(this::refreshColumns);
    }

}
