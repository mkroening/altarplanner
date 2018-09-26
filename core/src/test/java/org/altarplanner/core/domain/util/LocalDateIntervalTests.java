package org.altarplanner.core.domain.util;

import org.altarplanner.core.util.LocalDateInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
}
