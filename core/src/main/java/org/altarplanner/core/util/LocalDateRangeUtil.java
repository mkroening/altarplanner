package org.altarplanner.core.util;

import org.threeten.extra.LocalDateRange;

public class LocalDateRangeUtil {
    public static String getHyphenString(LocalDateRange dateRange) {
        return dateRange.getStart() + "--" + dateRange.getEnd();
    }
}
