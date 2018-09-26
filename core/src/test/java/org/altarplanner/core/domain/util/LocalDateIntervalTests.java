package org.altarplanner.core.domain.util;

import org.altarplanner.core.util.LocalDateInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static java.time.LocalDate.EPOCH;
import static org.junit.jupiter.api.Assertions.*;

public class LocalDateIntervalTests {
    private final static LocalDate TODAY = LocalDate.now();

    @Test
    void compareToTestSimple() {
        final var before = LocalDateInterval.of(TODAY, TODAY);
        final var after = LocalDateInterval.of(TODAY.plusDays(1), TODAY.plusDays(1));
        assertNotEquals(before, after);
        assertTrue(before.compareTo(after) > 0);
        assertTrue(after.compareTo(before) < 0);
    }

    @Test
    void compareToTestOverlap() {
        final var before = LocalDateInterval.of(TODAY.minusDays(1), TODAY);
        final var after = LocalDateInterval.of(TODAY, TODAY.plusDays(1));
        assertNotEquals(before, after);
        assertTrue(before.compareTo(after) > 0);
        assertTrue(after.compareTo(before) < 0);
    }

    @Test
    void compareToTestStartSame() {
        final var before = LocalDateInterval.of(TODAY, TODAY.plusDays(1));
        final var after = LocalDateInterval.of(TODAY, TODAY.plusDays(2));
        assertNotEquals(before, after);
        assertTrue(before.compareTo(after) > 0);
        assertTrue(after.compareTo(before) < 0);
    }

    @Test
    void compareToTestEndSame() {
        final var before = LocalDateInterval.of(TODAY.minusDays(2), TODAY);
        final var after = LocalDateInterval.of(TODAY.minusDays(1), TODAY);
        assertNotEquals(before, after);
        assertTrue(before.compareTo(after) > 0);
        assertTrue(after.compareTo(before) < 0);
    }

    @Test
    void equalsConsistenceTest() {
        final var one = LocalDateInterval.of(TODAY, TODAY);
        final var same = LocalDateInterval.of(TODAY, TODAY);
        assertNotSame(one, same);
        assertEquals(one, same);
    }

    @Test
    void toStringTest() {
        assertEquals("1970-01-01/1971-01-01", LocalDateInterval.of(EPOCH, EPOCH.plusYears(1)).toString());
        assertEquals("1970-01-01/02-01", LocalDateInterval.of(EPOCH, EPOCH.plusMonths(1)).toString());
        assertEquals("1970-01-01/02", LocalDateInterval.of(EPOCH, EPOCH.plusDays(1)).toString());
        assertEquals("1970-01-01/", LocalDateInterval.of(EPOCH, EPOCH).toString());
    }

    @Test
    void parseTest() {
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusYears(1)), LocalDateInterval.parse("1970-01-01/1971-01-01"));
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusMonths(1)), LocalDateInterval.parse("1970-01-01/02-01"));
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusDays(1)), LocalDateInterval.parse("1970-01-01/02"));
    }

    @Test
    void parseHyphenTest() {
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusYears(1)), LocalDateInterval.parseHyphen("1970-01-01--1971-01-01"));
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusMonths(1)), LocalDateInterval.parseHyphen("1970-01-01--02-01"));
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusDays(1)), LocalDateInterval.parseHyphen("1970-01-01--02"));
    }

    @Test
    void parseFormatterTest() {
        assertEquals(LocalDateInterval.of(EPOCH, EPOCH.plusYears(1)),
                LocalDateInterval.parse("Thursday, January 1, 1970 - Friday, January 1, 1971",
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL), " - "));
    }
}
