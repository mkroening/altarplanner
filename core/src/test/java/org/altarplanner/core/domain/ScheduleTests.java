package org.altarplanner.core.domain;

import org.altarplanner.core.domain.massLegacy.DiscreteMass;
import org.altarplanner.core.domain.massLegacy.RegularMass;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ScheduleTests {

    private final static LocalDate TODAY = LocalDate.now();

    @Test
    void constructorPublishedRelevanceTest() {
        final var config = new Config();
        final var firstMass = new DiscreteMass();
        firstMass.setDate(TODAY.minusWeeks(2));
        final var secondMass = new DiscreteMass();
        secondMass.setDate(TODAY);
        final var thirdMass = new DiscreteMass();
        thirdMass.setDate(TODAY.plusWeeks(2));
        final var publishedSchedule = new Schedule(config, List.of(firstMass, secondMass));
        final var schedule = new Schedule(config, List.of(thirdMass), publishedSchedule);
        assertEquals(1, schedule.getPublishedMasses().size());
        assertSame(publishedSchedule.getFinalDraftMasses().get(1), schedule.getPublishedMasses().get(0));
    }

    @Test
    void constructorPublishedFutureTest() {
        final var config = new Config();
        final var firstMass = new DiscreteMass();
        firstMass.setDate(TODAY.minusWeeks(6));
        final var secondMass = new DiscreteMass();
        secondMass.setDate(TODAY);
        final var publishedSchedule = new Schedule(config, List.of(secondMass));
        final var schedule = new Schedule(config, List.of(firstMass), publishedSchedule);
        assertEquals(1, schedule.getPublishedMasses().size());
        assertSame(publishedSchedule.getFinalDraftMasses().get(0), schedule.getPublishedMasses().get(0));
    }

    @Test
    void constructorFutureDraftTest() {
        final var config = new Config();
        final var regularMass = new RegularMass();
        regularMass.setDay(TODAY.getDayOfWeek());
        config.setRegularMasses(List.of(regularMass));
        final var firstMass = new DiscreteMass();
        firstMass.setDate(TODAY);
        final var secondMass = new DiscreteMass();
        secondMass.setDate(TODAY.plusWeeks(1));
        final var thirdMass = new DiscreteMass();
        thirdMass.setDate(TODAY.plusWeeks(2));
        final var lastSchedule = new Schedule(config, List.of(thirdMass));
        assertEquals(2, lastSchedule.getFutureDraftMasses().size());
        assertEquals(TODAY.getDayOfWeek(), lastSchedule.getFutureDraftMasses().get(0).getDate().getDayOfWeek());
        assertEquals(TODAY.getDayOfWeek(), lastSchedule.getFutureDraftMasses().get(1).getDate().getDayOfWeek());
        final var middleSchedule = new Schedule(config, List.of(secondMass), lastSchedule);
        assertEquals(1, middleSchedule.getFutureDraftMasses().size());
        assertEquals(TODAY.getDayOfWeek(), middleSchedule.getFutureDraftMasses().get(0).getDate().getDayOfWeek());
        final var firstSchedule = new Schedule(config, List.of(firstMass), middleSchedule);
        assertEquals(0, firstSchedule.getFutureDraftMasses().size());
    }

}
