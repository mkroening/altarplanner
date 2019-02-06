package org.altarplanner.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
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
    final var scheduleDir = Path.of("schedules/");
    Files.createDirectories(scheduleDir);
    fileChooser.setInitialDirectory(scheduleDir.toFile());

    final var input =
        Optional.ofNullable(fileChooser.showOpenDialog(fileChooserOwnerWindow)).map(File::toPath);
    if (input.isPresent()) {
      Schedule schedule = Schedule.unmarshal(input.get());
      LOGGER.info("Schedule has been loaded from {}", input);

      fileChooser.setTitle(Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.saveSchedule"));
      fileChooser
          .getExtensionFilters()
          .setAll(new FileChooser.ExtensionFilter("Excel 2007–2019 (.xlsx)", "*.xlsx"));
      final var exportedDir = Path.of("exported/");
      Files.createDirectories(exportedDir);
      fileChooser.setInitialDirectory(exportedDir.toFile());
      fileChooser.setInitialFileName(
          Launcher.RESOURCE_BUNDLE.getString("general.domain.schedule")
              + '_'
              + LocalDateRangeUtil.getHyphenString(schedule.getPlanningWindow())
              + ".xlsx");

      final var output =
          Optional.ofNullable(fileChooser.showSaveDialog(fileChooserOwnerWindow)).map(File::toPath);
      if (output.isPresent()) {
        if (FileSystems.getDefault().getPathMatcher("glob:**.xlsx").matches(output.get())) {
          Launcher.loadParent(
              "scheduleExporterXSSF.fxml",
              false,
              scheduleExporterXSSF -> {
                ((ScheduleExporterXSSF) scheduleExporterXSSF).setOutput(output.get());
                ((ScheduleExporterXSSF) scheduleExporterXSSF).setSchedule(schedule);
              });
        } else {
          LOGGER.info("{} has an unsupported file extension", output.get().getFileName());
        }
      } else {
        LOGGER.info("Schedule has not been exported, because no file to save to has been selected");
      }
    } else {
      LOGGER.info("Schedule has not been exported, because no file to load from has been selected");
    }
  }
}
