package org.altarplanner.core.xlsx;

import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.Service;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.util.DateTimeFormatterUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PoiIO {

    public static void exportSchedule(Schedule schedule, File file, int columns) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet();

        final XSSFCellStyle topOpenCellStyle = workbook.createCellStyle();
        topOpenCellStyle.setBorderLeft(BorderStyle.THIN);
        topOpenCellStyle.setBorderRight(BorderStyle.THIN);
        topOpenCellStyle.setBorderBottom(BorderStyle.THIN);

        final XSSFCellStyle bottomOpenCellStyle = workbook.createCellStyle();
        bottomOpenCellStyle.setBorderTop(BorderStyle.THIN);
        bottomOpenCellStyle.setBorderLeft(BorderStyle.THIN);
        bottomOpenCellStyle.setBorderRight(BorderStyle.THIN);

        final XSSFCellStyle topBottomOpenCellStyle = workbook.createCellStyle();
        topBottomOpenCellStyle.setBorderLeft(BorderStyle.THIN);
        topBottomOpenCellStyle.setBorderRight(BorderStyle.THIN);

        final CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        final XSSFCell headerCell = sheet.createRow(0).createCell(0);
        headerCell.setCellValue("Altar Plan: " + schedule.getPlanningWindow().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        headerCell.setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,2*(columns - 1)));

        final List<Integer> columnHeights = IntStream.range(0, columns).map(value -> 1).boxed().collect(Collectors.toList());

        schedule.getMasses().forEach(mass -> {
            int rowIndex = Collections.min(columnHeights);
            final int columnIndex = columnHeights.indexOf(rowIndex);
            final int rowsToCreate = rowIndex - sheet.getLastRowNum() + mass.getServices().size() + 2;
            IntStream.range(0, rowsToCreate).forEach(value -> sheet.createRow(sheet.getLastRowNum() + 1));
            rowIndex++;

            final XSSFCell dateTimeCell = sheet.getRow(rowIndex++).createCell(2*columnIndex);
            dateTimeCell.setCellValue(mass.getDateTimeString());
            dateTimeCell.setCellStyle(bottomOpenCellStyle);

            final XSSFCell churchFormCell = sheet.getRow(rowIndex++).createCell(2*columnIndex);
            churchFormCell.setCellValue(mass.getChurchFormString());
            churchFormCell.setCellStyle(topOpenCellStyle);

            for (final Service service : mass.getServices()) {
                XSSFCell serviceCell = sheet.getRow(rowIndex++).createCell(2*columnIndex);
                serviceCell.setCellValue(service.getDesc());
                serviceCell.setCellStyle(topBottomOpenCellStyle);
            }

            sheet.getRow(rowIndex - 1).getCell(2*columnIndex).setCellStyle(topOpenCellStyle);
            columnHeights.set(columnIndex, rowIndex);
        });

        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);
        sheet.setFitToPage(true);

        IntStream.range(0, columns).forEach(value -> sheet.autoSizeColumn(2*value));

        workbook.write(new FileOutputStream(file));

        workbook.close();
    }

    public static void exportScheduleOverview(Schedule schedule, File file) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet();

        final int rowOffset = 3;
        final int columnOffset = 2;
        final char firstColumnChar = 'A' + columnOffset;

        final XSSFRow dateRow = sheet.createRow(0);
        schedule.getDateMassesMap().forEach((date, masses) -> {
            final int firstMassColumn = columnOffset + schedule.getMasses().indexOf(masses.get(0));
            dateRow.createCell(firstMassColumn).setCellValue(date.format(DateTimeFormatterUtil.ISO_W_DAY));
            if (masses.size() > 1)
                sheet.addMergedRegion(new CellRangeAddress(0, 0, firstMassColumn, firstMassColumn + masses.size() - 1));
        });

        final XSSFRow timeChurchRow = sheet.createRow(1);
        IntStream.range(0, schedule.getMasses().size())
                .forEach(value -> {
                    PlanningMass mass = schedule.getMasses().get(value);
                    timeChurchRow.createCell(columnOffset + value)
                            .setCellValue(mass.getTime().format(DateTimeFormatterUtil.ISO_WO_SECONDS) + " - " + mass.getChurch());
                });

        final XSSFRow formRow = sheet.createRow(2);
        IntStream.range(0, schedule.getMasses().size())
                .forEach(value -> {
                    PlanningMass mass = schedule.getMasses().get(value);
                    formRow.createCell(columnOffset + value)
                            .setCellValue(mass.getForm());
                });

        IntStream.range(0, schedule.getServers().size())
                .forEach(serverIndex -> {
                    final int rowIndex = rowOffset + serverIndex;
                    final Server server = schedule.getServers().get(serverIndex);
                    final XSSFRow serverRow = sheet.createRow(rowIndex);
                    serverRow.createCell(0).setCellValue(server.getDesc());
                    serverRow.createCell(1).setCellFormula("COUNTA(" + firstColumnChar + (rowIndex + 1) + ":AMJ" + (rowIndex + 1) + ")");
                    IntStream.range(0, schedule.getMasses().size())
                            .forEach(massIndex -> schedule.getMasses().get(massIndex).getServices().parallelStream()
                                    .filter(service -> server.equals(service.getServer()))
                                    .findAny()
                                    .ifPresent(service -> serverRow.createCell(columnOffset + massIndex).setCellValue(service.getType().getId())));
                });

        sheet.createFreezePane(columnOffset,rowOffset);

        IntStream.range(0, columnOffset + schedule.getServers().size() - 1).forEach(sheet::autoSizeColumn);

        workbook.write(Files.newOutputStream(file.toPath()));
        workbook.close();
    }

}
