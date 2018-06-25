package org.altarplanner.core.util;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

    public final static DateTimeFormatter ISO_W_DAY = DateTimeFormatter.ofPattern("E yy-MM-dd");

    public final static DateTimeFormatter ISO_WO_SECONDS = DateTimeFormatter.ofPattern("HH:mm");

}
