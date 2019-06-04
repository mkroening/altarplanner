package org.altarplanner.core.persistence.poi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.altarplanner.core.planning.domain.planning.Server;
import org.altarplanner.core.planning.domain.planning.Service;
import org.altarplanner.core.planning.domain.state.Config;
import org.altarplanner.core.planning.domain.state.Schedule;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class POI {

  public static void exportScheduleCompact(Schedule schedule, Path output, int columns)
      throws IOException {
    try (final var workbook = new XSSFWorkbook()) {
      final var sheet =
          workbook.createSheet(Config.RESOURCE_BUNDLE.getString("altarServerSchedule"));

      final var format = workbook.createDataFormat();

      final var defaultFont = workbook.createFont();

      final var headingFont = workbook.createFont();
      headingFont.setFontHeightInPoints((short) 18);

      final var boldFont = workbook.createFont();
      boldFont.setBold(true);

      final var headingCellStyle = workbook.createCellStyle();
      headingCellStyle.setAlignment(HorizontalAlignment.CENTER);
      headingCellStyle.setFont(headingFont);

      final var dateTimeCellStyle = workbook.createCellStyle();
      dateTimeCellStyle.setBorderTop(BorderStyle.THIN);
      dateTimeCellStyle.setBorderLeft(BorderStyle.THIN);
      dateTimeCellStyle.setBorderRight(BorderStyle.THIN);
      dateTimeCellStyle.setDataFormat(
          format.getFormat(
              "NN, "
                  + DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                      FormatStyle.SHORT,
                      FormatStyle.SHORT,
                      IsoChronology.INSTANCE,
                      Locale.getDefault())));
      dateTimeCellStyle.setAlignment(HorizontalAlignment.LEFT);
      dateTimeCellStyle.setFont(boldFont);

      final var churchFormTopOpenCellStyle = workbook.createCellStyle();
      churchFormTopOpenCellStyle.setBorderLeft(BorderStyle.THIN);
      churchFormTopOpenCellStyle.setBorderRight(BorderStyle.THIN);
      churchFormTopOpenCellStyle.setBorderBottom(BorderStyle.THIN);
      churchFormTopOpenCellStyle.setFont(boldFont);

      final var churchFormTopBottomOpenCellStyle = workbook.createCellStyle();
      churchFormTopBottomOpenCellStyle.setBorderLeft(BorderStyle.THIN);
      churchFormTopBottomOpenCellStyle.setBorderRight(BorderStyle.THIN);
      churchFormTopBottomOpenCellStyle.setFont(boldFont);

      final var topBottomOpenCellStyle = workbook.createCellStyle();
      topBottomOpenCellStyle.setBorderLeft(BorderStyle.THIN);
      topBottomOpenCellStyle.setBorderRight(BorderStyle.THIN);
      topBottomOpenCellStyle.setFont(defaultFont);

      final var topOpenCellStyle = workbook.createCellStyle();
      topOpenCellStyle.setBorderLeft(BorderStyle.THIN);
      topOpenCellStyle.setBorderRight(BorderStyle.THIN);
      topOpenCellStyle.setBorderBottom(BorderStyle.THIN);
      topOpenCellStyle.setFont(defaultFont);

      final var headerCell = sheet.createRow(0).createCell(0);
      headerCell.setCellValue(
          Config.RESOURCE_BUNDLE.getString("altarServerSchedule")
              + ": "
              + schedule
                  .getPlanningWindow()
                  .getStart()
                  .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
              + " - "
              + schedule
                  .getPlanningWindow()
                  .getEndInclusive()
                  .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
      headerCell.setCellStyle(headingCellStyle);
      headerCell.getRow().setHeight((short) (headingFont.getFontHeight() + 82));
      if (columns > 1) {
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2 * (columns - 1)));
      }

      final List<Integer> columnHeights =
          IntStream.range(0, columns).map(value -> 1).boxed().collect(Collectors.toList());

      schedule
          .getFinalDraftMasses()
          .forEach(
              mass -> {
                int rowIndex = Collections.min(columnHeights);
                final int columnIndex = columnHeights.indexOf(rowIndex);
                int rowsToCreate = rowIndex - sheet.getLastRowNum() + mass.getServices().size() + 2;
                if (mass.getAnnotation() != null) {
                  ++rowsToCreate;
                }
                IntStream.range(0, rowsToCreate)
                    .forEach(value -> sheet.createRow(sheet.getLastRowNum() + 1));
                rowIndex++;

                final var dateTimeCell = sheet.getRow(rowIndex++).createCell(2 * columnIndex);
                final var date =
                    Date.from(mass.getDateTime().atZone(ZoneId.systemDefault()).toInstant());
                dateTimeCell.setCellValue(date);
                dateTimeCell.setCellStyle(dateTimeCellStyle);

                final var churchFormCell = sheet.getRow(rowIndex++).createCell(2 * columnIndex);
                churchFormCell.setCellValue(mass.getChurch() + " - " + mass.getForm());
                churchFormCell.setCellStyle(churchFormTopOpenCellStyle);

                if (mass.getAnnotation() != null) {
                  churchFormCell.setCellStyle(churchFormTopBottomOpenCellStyle);
                  final var annotationCell = sheet.getRow(rowIndex++).createCell(2 * columnIndex);
                  annotationCell.setCellValue(mass.getAnnotation());
                  annotationCell.setCellStyle(churchFormTopOpenCellStyle);
                }

                final var orderedServices =
                    mass.getServices().stream().sorted().collect(Collectors.toUnmodifiableList());
                for (final Service service : orderedServices) {
                  final var serviceCell = sheet.getRow(rowIndex++).createCell(2 * columnIndex);
                  serviceCell.setCellValue(service.getDesc());
                  serviceCell.setCellStyle(topBottomOpenCellStyle);
                }

                sheet.getRow(rowIndex - 1).getCell(2 * columnIndex).setCellStyle(topOpenCellStyle);
                columnHeights.set(columnIndex, rowIndex);
              });

      sheet.getPrintSetup().setFitWidth((short) 1);
      sheet.getPrintSetup().setFitHeight((short) 0);
      sheet.setFitToPage(true);

      IntStream.range(0, columns).forEach(value -> sheet.autoSizeColumn(2 * value));

      try (final var outputStream = Files.newOutputStream(output)) {
        workbook.write(outputStream);
      }
    }
  }

  public static void exportScheduleOverview(Schedule schedule, Path output) throws IOException {
    try (final var workbook = new XSSFWorkbook()) {
      final var sheet = workbook.createSheet();
      final var format = workbook.createDataFormat();
      final var isoLocalDateTimeWithoutSecondsCellStyle = workbook.createCellStyle();
      isoLocalDateTimeWithoutSecondsCellStyle.setDataFormat(
          format.getFormat("NN, YYYY-MM-DD HH:MM"));
      isoLocalDateTimeWithoutSecondsCellStyle.setAlignment(HorizontalAlignment.LEFT);

      final int rowOffset = 3;
      final int columnOffset = 2;
      final char firstColumnChar = 'A' + columnOffset;

      final var dateTimeRow = sheet.createRow(0);
      final var churchFormRow = sheet.createRow(1);
      final var annotationRow = sheet.createRow(2);
      annotationRow.createCell(0).setCellValue(Config.RESOURCE_BUNDLE.getString("altarServer"));
      annotationRow.createCell(1).setCellValue(Config.RESOURCE_BUNDLE.getString("assignmentCount"));
      IntStream.range(0, schedule.getFinalDraftMasses().size())
          .forEach(
              i -> {
                final var mass = schedule.getFinalDraftMasses().get(i);
                final var date =
                    Date.from(mass.getDateTime().atZone(ZoneId.systemDefault()).toInstant());
                final var dateTimeCell = dateTimeRow.createCell(columnOffset + i);
                dateTimeCell.setCellValue(date);
                dateTimeCell.setCellStyle(isoLocalDateTimeWithoutSecondsCellStyle);
                churchFormRow
                    .createCell(columnOffset + i)
                    .setCellValue(mass.getChurch() + " - " + mass.getForm());
                annotationRow.createCell(columnOffset + i).setCellValue(mass.getAnnotation());
              });

      IntStream.range(0, schedule.getServers().size())
          .forEach(
              serverIndex -> {
                final int rowIndex = rowOffset + serverIndex;
                final Server server = schedule.getServers().get(serverIndex);
                final var serverRow = sheet.createRow(rowIndex);
                serverRow.createCell(0).setCellValue(server.getDesc());
                serverRow
                    .createCell(1)
                    .setCellFormula(
                        "COUNTA("
                            + firstColumnChar
                            + (rowIndex + 1)
                            + ":AMJ"
                            + (rowIndex + 1)
                            + ")");
                IntStream.range(0, schedule.getFinalDraftMasses().size())
                    .forEach(
                        massIndex ->
                            schedule
                                .getFinalDraftMasses()
                                .get(massIndex)
                                .getServices()
                                .parallelStream()
                                .filter(service -> server.equals(service.getServer()))
                                .findAny()
                                .ifPresent(
                                    service -> {
                                      final var serviceType = service.getType();
                                      final var serviceString =
                                          serviceType.getName()
                                              + "_"
                                              + serviceType.getMaxYear()
                                              + "-"
                                              + serviceType.getMinYear();
                                      serverRow
                                          .createCell(columnOffset + massIndex)
                                          .setCellValue(serviceString);
                                    }));
              });

      sheet.createFreezePane(columnOffset, rowOffset);
      sheet.setAutoFilter(
          new CellRangeAddress(
              rowOffset - 1,
              rowOffset - 1 + schedule.getServers().size() - 1,
              0,
              columnOffset + schedule.getFinalDraftMasses().size() - 1));

      IntStream.range(0, columnOffset + schedule.getServers().size() - 1)
          .forEach(sheet::autoSizeColumn);

      try (final var outputStream = Files.newOutputStream(output)) {
        workbook.write(outputStream);
      }
    }
  }

  public static List<String> readHeader(final Path input) {
    try (final var workbook =
        new XSSFWorkbook(OPCPackage.open(input.toFile(), PackageAccess.READ))) {
      final var headerRow = workbook.getSheetAt(0).getRow(0);
      Objects.requireNonNull(headerRow, "Header row at sheet index 0 must not be null.");
      final List<Cell> cells = new ArrayList<>(headerRow.getLastCellNum());
      headerRow.forEach(cells::add);
      return cells.stream()
          .map(Cell::getStringCellValue)
          .filter(Predicate.not(String::isBlank))
          .collect(Collectors.toUnmodifiableList());
    } catch (final IOException | InvalidFormatException e) {
      throw new IllegalArgumentException(
          "Could not read the file (" + input.getFileName() + ").", e);
    }
  }

  public static List<Server> readServers(
      final Path input,
      final int surnameColumnIndex,
      final int forenameColumnIndex,
      final int yearColumnIndex,
      final Map<DayOfWeek, Integer> absentOnDayOfWeekColumnIndices) {
    try (final var workbook =
        new XSSFWorkbook(OPCPackage.open(input.toFile(), PackageAccess.READ))) {
      final var sheet = workbook.getSheetAt(0);
      return IntStream.range(1, sheet.getLastRowNum())
          .mapToObj(sheet::getRow)
          .map(
              row -> {
                final Server server = new Server();
                server.setSurname(row.getCell(surnameColumnIndex).getStringCellValue());
                server.setForename(row.getCell(forenameColumnIndex).getStringCellValue());
                server.setYear((int) row.getCell(yearColumnIndex).getNumericCellValue());
                server.setWeeklyAbsences(
                    absentOnDayOfWeekColumnIndices.entrySet().stream()
                        .filter(
                            regularAbsenceColumnIndexEntry ->
                                row.getCell(regularAbsenceColumnIndexEntry.getValue())
                                    .getBooleanCellValue())
                        .map(Entry::getKey)
                        .collect(Collectors.toUnmodifiableList()));
                return server;
              })
          .collect(Collectors.toUnmodifiableList());
    } catch (final IOException | InvalidFormatException e) {
      throw new IllegalArgumentException(
          "Could not read the file (" + input.getFileName() + ").", e);
    }
  }
}
