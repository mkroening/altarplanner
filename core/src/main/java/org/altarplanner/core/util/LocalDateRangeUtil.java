package org.altarplanner.core.util;

import org.threeten.extra.LocalDateRange;

import java.util.Comparator;

public class LocalDateRangeUtil {

    public static final Comparator<LocalDateRange> RECENCY_COMPARATOR = Comparator
            .comparing(LocalDateRange::getEnd)
            .thenComparing(LocalDateRange::getStart)
            .reversed();

    public static String getHyphenString(LocalDateRange dateRange) {
        return dateRange.getStart() + "--" + dateRange.getEnd();
    }

}
