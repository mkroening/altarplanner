package org.altarplanner.core.util;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

  public static final DateTimeFormatter ISO_DATE_WITH_DAY_WITH_SHORT_YEAR =
      DateTimeFormatter.ofPattern("E yy-MM-dd");

  public static final DateTimeFormatter ISO_LOCAL_TIME_WITHOUT_SECONDS =
      DateTimeFormatter.ofPattern("HH:mm");

  public static final DateTimeFormatter ISO_LOCAL_DATE_TIME_WITHOUT_SECONDS =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
}
