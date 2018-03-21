package org.altarplanner.core.io;

import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.Service;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ODS {

    private static final Border BORDER = new Border(Color.BLACK, 0.5, StyleTypeDefinitions.SupportedLinearMeasure.PT);

    private static int writeMassToTable(PlanningMass planningMass, Table table, int colIndex, int rowIndex) {
        Cell cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(planningMass.getDateTimeString());
        cell.setBorders(StyleTypeDefinitions.CellBordersType.TOP, BORDER);
        cell.setBorders(StyleTypeDefinitions.CellBordersType.LEFT_RIGHT, BORDER);
        rowIndex++;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(planningMass.getChurchFormString());
        cell.setBorders(StyleTypeDefinitions.CellBordersType.LEFT_RIGHT, BORDER);
        cell.setBorders(StyleTypeDefinitions.CellBordersType.BOTTOM, BORDER);
        rowIndex++;

        planningMass.getServices().sort(Service.getDescComparator());

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setBorders(StyleTypeDefinitions.CellBordersType.TOP, BORDER);

        int finalRowIndex = rowIndex;
        IntStream.range(0, planningMass.getServices().size()).forEach(value -> {
            Cell serviceCell = table.getCellByPosition(colIndex, finalRowIndex + value);
            serviceCell.setStringValue(planningMass.getServices().get(value).getDesc());
            serviceCell.setBorders(StyleTypeDefinitions.CellBordersType.LEFT_RIGHT, BORDER);
        });
        rowIndex += planningMass.getServices().size() - 1;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setBorders(StyleTypeDefinitions.CellBordersType.BOTTOM, BORDER);
        rowIndex += 2;

        return rowIndex;
    }

    public static void exportSchedule(Schedule schedule, File file, int columns) throws Exception {
        SpreadsheetDocument spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
        Table table = spreadsheetDocument.getSheetByIndex(0);
        table.setTableName(Config.RESOURCE_BUNDLE.getString("altarServerSchedule"));

        List<Integer> columnHeights = IntStream.range(0, columns).map(value -> 0).boxed().collect(Collectors.toList());

        IntStream.range(0, columns).forEach(value -> table.getColumnByIndex(2 * value).setUseOptimalWidth(true));

        schedule.getMasses().forEach(planningMass -> {
            int row = Collections.min(columnHeights);
            int column = columnHeights.indexOf(row);
            columnHeights.set(column, writeMassToTable(planningMass, table, 2 * column, row));
        });

        spreadsheetDocument.save(file);
        LoggerFactory.getLogger(ODS.class).info("Spreadsheet saved (file:{})", file);
    }

}
