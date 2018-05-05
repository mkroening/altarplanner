package org.altarplanner.app.planning;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.io.XML;
import org.altarplanner.core.solver.ScheduleSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class SolverView {

    @FXML private Label scoreLabel;

    private ScheduleSolver solver = new ScheduleSolver();

    private static final Logger LOGGER = LoggerFactory.getLogger(SolverView.class);

    @FXML private void initialize() {
        solver.addNewBestUiScoreStringConsumer(s -> Platform.runLater(() -> scoreLabel.setText(s)));
    }

    public void initData(Config config, List<DiscreteMass> masses) {
        new Thread(() -> {
            Schedule solved = solver.solve(new Schedule(null, masses, config));
            Platform.runLater(() -> saveSchedule(solved));
        }).start();
    }

    private void saveSchedule(Schedule schedule) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("saveSchedule"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("schedules/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);
        fileChooser.setInitialFileName(schedule.getPlanningWindow().getStart() + "_" + schedule.getPlanningWindow().getEnd() + ".xml");

        File selectedFile = fileChooser.showSaveDialog(scoreLabel.getParent().getScene().getWindow());
        if (selectedFile != null) {
            try {
                XML.write(schedule, selectedFile);
                LOGGER.info("Schedule has been saved as {}", selectedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else LOGGER.info("Schedule has not been saved, because no file has been selected");
    }

    public void stopPlanning() {
        solver.terminateEarly();
    }

}
