package org.altarplanner.app;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.altarplanner.app.planning.SolverView;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.xlsx.PoiIO;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnexpectedElementException;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.altarplanner.core.xml.jaxb.util.DiscreteMassCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Launcher extends Application {

    public static final Config CONFIG;
    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.app.locale.locale");
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
    private static Stage primaryStage;

    static {
        Config config = new Config();
        try {
            config = Config.load();
        } catch (UnknownJAXBException e) {
            e.printStackTrace();
        }
        CONFIG = config;
    }

    public static void loadParent(String location, boolean inPrimaryStage, Consumer<Object> controllerConsumer) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(location), RESOURCE_BUNDLE);
        Parent root = loader.load();
        Object controller = loader.getController();

        controllerConsumer.accept(controller);

        String name = controller.getClass().getSimpleName();
        String key = name.substring(0, 1).toLowerCase() + name.substring(1) + ".windowTitle";
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

    @Override
    public void start(Stage primaryStage) throws IOException {
        Launcher.primaryStage = primaryStage;
        loadParent("launcher.fxml", true);
    }

    @FXML private void editServiceTypes() throws IOException {
        loadParent("config/serviceTypeEditor.fxml", true);
    }

    @FXML private void editRegularMasses() throws IOException {
        loadParent("config/regularMassEditor.fxml", true);
    }

    @FXML private void editServers() throws IOException {
        loadParent("config/serverEditor.fxml", true);
    }

    @FXML private void createDiscreteMasses() throws IOException {
        loadParent("planning/discreteMassEditor.fxml", true);
    }

    @FXML private void planServices() throws IOException, UnknownJAXBException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.openDiscreteMasses"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("masses/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                List<DiscreteMass> masses = JaxbIO.unmarshal(selectedFile, DiscreteMassCollection.class).getDiscreteMasses();
                LOGGER.info("Masses have been loaded from {}", selectedFile);
                loadParent("planning/solverView.fxml", true, solverView -> ((SolverView)solverView).solve(new Schedule(null, masses, CONFIG)));
            } catch (UnexpectedElementException e) {
                LOGGER.error("No masses could have been loaded. Please try a different file!");
            }
        }
        else LOGGER.info("No masses have been loaded, because no file has been selected");
    }

    @FXML private void exportSchedule() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.openSchedule"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File directory = new File("schedules/");
        directory.mkdirs();
        fileChooser.setInitialDirectory(directory);

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                Schedule schedule = Schedule.load(selectedFile);
                LOGGER.info("Schedule has been loaded from {}", selectedFile);

                fileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("ODF Spreadsheet (.ods)", "*.xlsx"));
                directory = new File("exported/");
                directory.mkdirs();
                fileChooser.setInitialDirectory(directory);
                fileChooser.setInitialFileName(schedule.getPlanningWindow().getStart() + "_" + schedule.getPlanningWindow().getEnd() + ".xlsx");

                selectedFile = fileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    PoiIO.exportSchedule(schedule, selectedFile, 3);
                    LOGGER.info("Schedule has been exported as {}", selectedFile);
                } else LOGGER.info("Schedule has not been exported, because no file to save to has been selected");
            } catch (UnexpectedElementException e) {
                LOGGER.error("Schedule could not have been loaded. Please try a different file!");
            }
        } else LOGGER.info("Schedule has not been exported, because no file to load from has been selected");
    }

}
