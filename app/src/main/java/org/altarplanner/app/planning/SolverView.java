package org.altarplanner.app.planning;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.solver.ScheduleSolver;
import org.altarplanner.core.util.LocalDateRangeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SolverView {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolverView.class);

    @FXML private Label scoreLabel;

    private ScheduleSolver solver = new ScheduleSolver();

    @FXML private void initialize() {
        solver.addNewBestUiScoreStringConsumer(s -> Platform.runLater(() -> scoreLabel.setText(s)));
    }

    public void solve(Schedule schedule) {
        new Thread(() -> {
            Schedule solved = solver.solve(schedule);
            Platform.runLater(() -> saveSchedule(solved));
        }).start();
    }

    private void saveSchedule(Schedule schedule) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("schedules/");
        try {
            Files.createDirectories(directory.toPath());
            fileChooser.setInitialDirectory(directory);
            fileChooser.setInitialFileName(Launcher.RESOURCE_BUNDLE.getString("general.domain.schedule") + '_' + LocalDateRangeUtil.getHyphenString(schedule.getPlanningWindow()) + ".xml");

            File selectedFile = fileChooser.showSaveDialog(scoreLabel.getParent().getScene().getWindow());
            if (selectedFile != null) {
                schedule.marshal(selectedFile.toPath());
                LOGGER.info("Schedule has been saved as {}", selectedFile);
                Launcher.loadParent("launcher.fxml", true);
            } else LOGGER.info("Schedule has not been saved, because no file has been selected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void stopPlanning() {
        solver.terminateEarly();
    }

}
