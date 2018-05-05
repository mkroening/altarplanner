package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.altarplanner.app.config.RegularMassEditor;
import org.altarplanner.app.config.ServerEditor;
import org.altarplanner.app.config.ServiceTypeEditor;
import org.altarplanner.app.planning.DiscreteMassEditor;
import org.altarplanner.app.planning.SolverView;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.io.ODS;
import org.altarplanner.core.io.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Launcher extends Application {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscreteMassEditor.class);
    private static Stage primaryStage;

    public static void loadParent(String location, boolean inPrimaryStage, Consumer<Object> controllerConsumer) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(location), RESOURCE_BUNDLE);
        Parent root = loader.load();
        Object controller = loader.getController();

        controllerConsumer.accept(controller);

        String name = controller.getClass().getSimpleName();
        String key = name.substring(0, 1).toLowerCase() + name.substring(1);
        String title = RESOURCE_BUNDLE.getString(key);

        Stage stage;
        if (inPrimaryStage) {
            stage = primaryStage;
        } else {
            stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        stage.hide();
        stage.setTitle(title);
        stage.setMinHeight(root.minHeight(-1));
        stage.setMinWidth(root.minWidth(-1));
        stage.setHeight(root.prefHeight(-1));
        stage.setWidth(root.prefWidth(-1));
        stage.setMaxHeight(root.maxHeight(-1));
        stage.setMaxWidth(root.maxWidth(-1));
        if (stage.getScene() != null) {
            stage.setX(stage.getX());
            stage.setY(stage.getY());
            stage.getScene().setRoot(root);
        } else {
            Scene scene = new Scene(root);
            scene.getStylesheets().add("org/altarplanner/app/style.css");
            stage.setScene(scene);
        }
        stage.show();
    }

    public static void loadParent(String location, boolean inPrimaryStage) throws IOException {
        loadParent(location, inPrimaryStage, controller -> {});
    }

    private Config config;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Launcher.primaryStage = primaryStage;
        loadParent("launcher.fxml", true, launcher -> ((Launcher)launcher).initData(Config.load()));
    }

    public void initData(Config config) {
        this.config = config;
    }

    public void loadServiceTypeEditor() throws IOException {
        loadParent("config/serviceTypeEditor.fxml", true, serviceTypeEditor -> ((ServiceTypeEditor)serviceTypeEditor).initData(config));
    }

    public void loadRegularMassEditor() throws IOException {
        loadParent("config/regularMassEditor.fxml", true, regularMassEditor -> ((RegularMassEditor)regularMassEditor).initData(config));
    }

    public void loadServerEditor() throws IOException {
        loadParent("config/serverEditor.fxml", true, serverEditor -> ((ServerEditor)serverEditor).initData(config));
    }

    public void loadDiscreteMassEditor() throws IOException {
        loadParent("planning/discreteMassEditor.fxml", true, discreteMassEditor -> ((DiscreteMassEditor)discreteMassEditor).initData(config));
    }

    public void loadSolverView() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("openDiscreteMasses"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("masses/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            List<DiscreteMass> masses = XML.readList(selectedFile, DiscreteMass.class);
            LOGGER.info("Masses have been loaded from {}", selectedFile);
            loadParent("planning/solverView.fxml", true, solverView -> ((SolverView)solverView).initData(config, masses));
        }
        else LOGGER.info("No masses have been loaded, because no file has been selected");
    }

    public void exportSchedule() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("openSchedule"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("schedules/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            Schedule schedule = XML.read(selectedFile, Schedule.class);
            LOGGER.info("Schedule has been loaded from {}", selectedFile);

            fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("saveSchedule"));
            fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("ODF Spreadsheet (.ods)", "*.ods"));
            directory = new File("exported/");
            directory.mkdirs();
            fileChooser.setInitialDirectory(directory);
            fileChooser.setInitialFileName(schedule.getPlanningWindow().getStart() + "_" + schedule.getPlanningWindow().getEnd() + ".ods");

            selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile != null) {
                ODS.exportSchedule(schedule, selectedFile, 3);
                LOGGER.info("Schedule has been exported as {}", selectedFile);
            } else LOGGER.info("Schedule has not been exported, because no file to save to has been selected");
        } else LOGGER.info("Schedule has not been exported, because no file to load from has been selected");
    }
}
