package org.altarplanner.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.bind.UnmarshalException;
import org.altarplanner.core.domain.state.Schedule;
import org.altarplanner.core.util.LocalDateRangeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleExporter.class);

  public static void exportSchedule(final Window fileChooserOwnerWindow)
      throws IOException, UnmarshalException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.openSchedule"));
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
    File directory = new File("schedules/");
    Files.createDirectories(directory.toPath());
    fileChooser.setInitialDirectory(directory);

    File selectedFile = fileChooser.showOpenDialog(fileChooserOwnerWindow);
    if (selectedFile != null) {
      Schedule schedule = Schedule.unmarshal(selectedFile.toPath());
      LOGGER.info("Schedule has been loaded from {}", selectedFile);

      fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
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

      selectedFile = fileChooser.showSaveDialog(fileChooserOwnerWindow);
      if (selectedFile != null) {
        if (selectedFile.getName().endsWith(".xlsx")) {
          final var output = selectedFile;
          Launcher.loadParent("scheduleExporterXSSF.fxml", false, scheduleExporterXSSF -> {
            ((ScheduleExporterXSSF) scheduleExporterXSSF).setOutput(output);
            ((ScheduleExporterXSSF) scheduleExporterXSSF).setSchedule(schedule);
          });
        } else {
          LOGGER.info("{} has an unsupported file extension", selectedFile);
        }
      } else {
        LOGGER.info("Schedule has not been exported, because no file to save to has been selected");
      }
    } else {
      LOGGER.info("Schedule has not been exported, because no file to load from has been selected");
    }
  }
}
