package org.altarplanner.app.planning;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.solver.ScheduleSolver;

public class SolverView {

    @FXML private Label scoreLabel;

    private Schedule schedule;

    public void initData(Schedule schedule) {
        this.schedule = schedule;

        new Thread(this::plan).start();
    }

    private void plan() {
        ScheduleSolver scheduleSolver = new ScheduleSolver();
        scheduleSolver.addNewBestUiScoreStringConsumer(s -> Platform.runLater(() -> scoreLabel.setText(s)));
        schedule = scheduleSolver.solve(schedule);
    }

}
