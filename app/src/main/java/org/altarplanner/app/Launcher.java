package org.altarplanner.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.UnmarshalException;
import org.altarplanner.app.planning.SolverView;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.ScheduleTemplate;
import org.altarplanner.core.util.LocalDateRangeUtil;
import org.altarplanner.core.xlsx.PoiIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher extends Application {

  public static final Path CONFIG_PATH = Path.of("config.xml");
  public static final Config CONFIG;
  public static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle("org.altarplanner.app.locale.locale");
  private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
  private static Stage primaryStage;

  static {
    Config tmpConfig;
    if (Files.exists(CONFIG_PATH)) {
      try {
        tmpConfig = Config.unmarshal(CONFIG_PATH);
        LOGGER.info("Successfully loaded {}", CONFIG_PATH);
      } catch (UnmarshalException e) {
        e.printStackTrace();
        LOGGER.warn("{} corrupt!", CONFIG_PATH);
        final var corruptPath =
            Path.of(
                "config_corrupt_"
                    + LocalDateTime.now()
                        .truncatedTo(ChronoUnit.SECONDS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    + ".xml");
        LOGGER.warn("Moving \"{}\" to \"{}\"", CONFIG_PATH, corruptPath);
        try {
          Files.move(CONFIG_PATH, corruptPath);
        } catch (IOException e1) {
          e1.printStackTrace();
          LOGGER.error("Move failed.");
          System.exit(1);
        }
        LOGGER.info("Creating new config.");
        tmpConfig = new Config();
        tmpConfig.marshal(CONFIG_PATH);
      }
    } else {
      LOGGER.warn("{} does not exist, creating new Config", CONFIG_PATH);
      tmpConfig = new Config();
    }
    CONFIG = tmpConfig;
  }

  private static Bounds getBounds(
      final Node node,
      final BiFunction<Node, Double, Double> widthFunction,
      final BiFunction<Node, Double, Double> heightFunction) {
    final Orientation bias = node.getContentBias();
    double prefWidth;
    double prefHeight;
    if (bias == Orientation.HORIZONTAL) {
      prefWidth = widthFunction.apply(node, (double) -1);
      prefHeight = heightFunction.apply(node, prefWidth);
    } else if (bias == Orientation.VERTICAL) {
      prefHeight = heightFunction.apply(node, (double) -1);
      prefWidth = widthFunction.apply(node, prefHeight);
    } else {
      prefWidth = widthFunction.apply(node, (double) -1);
      prefHeight = heightFunction.apply(node, (double) -1);
    }
    return new BoundingBox(0, 0, prefWidth, prefHeight);
  }

  private static void applyRootSizeConstraints(final Stage stage) {
    final Parent root = stage.getScene().getRoot();
    stage.sizeToScene();
    final double deltaWidth = stage.getWidth() - root.getLayoutBounds().getWidth();
    final double deltaHeight = stage.getHeight() - root.getLayoutBounds().getHeight();
    final Bounds minBounds = getBounds(root, Node::minWidth, Node::minHeight);
    stage.setMinWidth(minBounds.getWidth() + deltaWidth);
    stage.setMinHeight(minBounds.getHeight() + deltaHeight);
    final Bounds prefBounds = getBounds(root, Node::prefWidth, Node::prefHeight);
    stage.setWidth(prefBounds.getWidth() + deltaWidth);
    stage.setHeight(prefBounds.getHeight() + deltaHeight);
    final Bounds maxBounds = getBounds(root, Node::maxWidth, Node::maxHeight);
    stage.setMaxWidth(maxBounds.getWidth() + deltaWidth);
    stage.setMaxHeight(maxBounds.getHeight() + deltaHeight);
  }

  public static void loadParent(
      String location, boolean inPrimaryStage, Consumer<Object> controllerConsumer)
      throws IOException {
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
    applyRootSizeConstraints(stage);
  }

  public static void loadParent(String location, boolean inPrimaryStage) throws IOException {
    loadParent(location, inPrimaryStage, controller -> {});
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Launcher.primaryStage = primaryStage;
    loadParent("launcher.fxml", true);
  }

  @FXML
  private void editServiceTypes() throws IOException {
    loadParent("config/serviceTypeEditor.fxml", true);
  }

  @FXML
  private void editRegularMasses() throws IOException {
    loadParent("config/regularMassEditor.fxml", true);
  }

  @FXML
  private void editServers() throws IOException {
    loadParent("config/serverEditor.fxml", true);
  }

  @FXML
  private void editScheduleTemplate() throws IOException {
    loadParent("planning/scheduleTemplateEditor.fxml", true);
  }

  @FXML
  private void createSchedule() throws IOException, UnmarshalException {
    final FileChooser massFileChooser = new FileChooser();
    massFileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.openScheduleTemplate"));
    massFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
    final File massDirectory = new File("masses/");
    Files.createDirectories(massDirectory.toPath());
    massFileChooser.setInitialDirectory(massDirectory);
    final File massFile = massFileChooser.showOpenDialog(primaryStage);

    if (massFile != null) {
      final var scheduleTemplate = ScheduleTemplate.unmarshal(massFile.toPath());
      LOGGER.info("Masses have been loaded from {}", massFile);

      final FileChooser lastScheduleFileChooser = new FileChooser();
      lastScheduleFileChooser.setTitle(
          RESOURCE_BUNDLE.getString("fileChooserTitle.openLastSchedule"));
      lastScheduleFileChooser
          .getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
      final File scheduleDirectory = new File("schedules/");
      Files.createDirectories(scheduleDirectory.toPath());
      lastScheduleFileChooser.setInitialDirectory(scheduleDirectory);
      final File lastScheduleFile = lastScheduleFileChooser.showOpenDialog(primaryStage);

      final Schedule createdSchedule;
      if (lastScheduleFile != null) {
        final Schedule lastSchedule = Schedule.unmarshal(lastScheduleFile.toPath());
        createdSchedule = new Schedule(scheduleTemplate, lastSchedule, CONFIG);
        LOGGER.info("Last Schedule has been loaded from {}", lastScheduleFile);
      } else {
        createdSchedule = new Schedule(scheduleTemplate, CONFIG);
        LOGGER.info("Last Schedule has not been selected");
      }

      final FileChooser createdScheduleFileChooser = new FileChooser();
      createdScheduleFileChooser.setTitle(
          Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
      createdScheduleFileChooser
          .getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
      final File schedulesDirectory = new File("schedules/");
      Files.createDirectories(schedulesDirectory.toPath());
      createdScheduleFileChooser.setInitialDirectory(schedulesDirectory);
      createdScheduleFileChooser.setInitialFileName(
          Launcher.RESOURCE_BUNDLE.getString("general.domain.schedule")
              + '_'
              + LocalDateRangeUtil.getHyphenString(createdSchedule.getPlanningWindow())
              + ".xml");
      final File createdScheduleFile = createdScheduleFileChooser.showSaveDialog(primaryStage);

      if (createdScheduleFile != null) {
        createdSchedule.marshal(createdScheduleFile.toPath());
        LOGGER.info("Schedule has been saved as {}", createdScheduleFile);
      } else {
        LOGGER.info("Schedule has not been saved, because no file has been selected");
      }
    } else {
      LOGGER.info("No masses have been loaded, because no file has been selected");
    }
  }

  @FXML
  private void planSchedule() throws IOException, UnmarshalException {
    final FileChooser lastScheduleFileChooser = new FileChooser();
    lastScheduleFileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.openSchedule"));
    lastScheduleFileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
    final File scheduleDirectory = new File("schedules/");
    Files.createDirectories(scheduleDirectory.toPath());
    lastScheduleFileChooser.setInitialDirectory(scheduleDirectory);
    final File scheduleFile = lastScheduleFileChooser.showOpenDialog(primaryStage);

    if (scheduleFile != null) {
      final Schedule schedule = Schedule.unmarshal(scheduleFile.toPath());
      LOGGER.info("Schedule has been loaded from {}", scheduleFile);
      loadParent(
          "planning/solverView.fxml",
          true,
          solverView -> ((SolverView) solverView).solve(schedule));
    } else {
      LOGGER.info("Schedule has not been loaded, because no file has been selected");
    }
  }

  @FXML
  private void exportSchedule() throws Exception {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.openSchedule"));
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
    File directory = new File("schedules/");
    Files.createDirectories(directory.toPath());
    fileChooser.setInitialDirectory(directory);

    File selectedFile = fileChooser.showOpenDialog(primaryStage);
    if (selectedFile != null) {
      Schedule schedule = Schedule.unmarshal(selectedFile.toPath());
      LOGGER.info("Schedule has been loaded from {}", selectedFile);

      fileChooser.setTitle(RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
      fileChooser
          .getExtensionFilters()
          .setAll(new FileChooser.ExtensionFilter("Excel 2007–2019 (.xlsx)", "*.xlsx"));
      directory = new File("exported/");
      Files.createDirectories(directory.toPath());
      fileChooser.setInitialDirectory(directory);
      fileChooser.setInitialFileName(
          Launcher.RESOURCE_BUNDLE.getString("general.domain.schedule")
              + '_'
              + LocalDateRangeUtil.getHyphenString(schedule.getPlanningWindow())
              + ".xlsx");

      selectedFile = fileChooser.showSaveDialog(primaryStage);
      if (selectedFile != null) {
        PoiIO.exportSchedule(schedule, selectedFile, 3);
        LOGGER.info("Schedule has been exported as {}", selectedFile);
      } else {
        LOGGER.info("Schedule has not been exported, because no file to save to has been selected");
      }
    } else {
      LOGGER.info("Schedule has not been exported, because no file to load from has been selected");
    }
  }
}
