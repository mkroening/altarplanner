package org.altarplanner.app.planning;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.solver.ScheduleSolver;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SolverView {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolverView.class);

    @FXML private Label scoreLabel;

    private Config config;
    private ScheduleSolver solver = new ScheduleSolver();

    @FXML private void initialize() {
        solver.addNewBestUiScoreStringConsumer(s -> Platform.runLater(() -> scoreLabel.setText(s)));
    }

    public void initData(Config config, List<DiscreteMass> masses) {
        this.config = config;
        new Thread(() -> {
            Schedule solved = solver.solve(new Schedule(null, masses, config));
            Platform.runLater(() -> saveSchedule(solved));
        }).start();
    }

    private void saveSchedule(Schedule schedule) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("schedules/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);
        fileChooser.setInitialFileName(schedule.getPlanningWindow().getStart() + "_" + schedule.getPlanningWindow().getEnd() + ".xml");

        File selectedFile = fileChooser.showSaveDialog(scoreLabel.getParent().getScene().getWindow());
        if (selectedFile != null) {
            try {
                JaxbIO.marshal(schedule, selectedFile);
                LOGGER.info("Schedule has been saved as {}", selectedFile);
                Launcher.loadParent("launcher.fxml", true, launcher -> ((Launcher)launcher).initData(config));
            } catch (IOException | UnknownJAXBException e) {
                e.printStackTrace();
            }
        } else LOGGER.info("Schedule has not been saved, because no file has been selected");
    }

    public void stopPlanning() {
        solver.terminateEarly();
    }

}
