package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DatedDraftMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ScheduleTests {

    private final static LocalDate TODAY = LocalDate.now();

    @Test
    void constructorPublishedRelevanceTest() {
        final var config = new Config();
        final var firstMass = new DatedDraftMass();
        firstMass.setDateTime(LocalDateTime.of(TODAY.minusWeeks(2), LocalTime.of(11,0)));
        final var secondMass = new DatedDraftMass();
        secondMass.setDateTime(LocalDateTime.of(TODAY, LocalTime.of(11,0)));
        final var thirdMass = new DatedDraftMass();
        thirdMass.setDateTime(LocalDateTime.of(TODAY.plusWeeks(2), LocalTime.of(11,0)));
        final var publishedSchedule = new Schedule(config, List.of(firstMass, secondMass));
        final var schedule = new Schedule(config, List.of(thirdMass), publishedSchedule);
        assertEquals(1, schedule.getPublishedMasses().size());
        assertSame(publishedSchedule.getFinalDraftMasses().get(1), schedule.getPublishedMasses().get(0));
    }

    @Test
    void constructorPublishedFutureTest() {
        final var config = new Config();
        final var firstMass = new DatedDraftMass();
        firstMass.setDateTime(LocalDateTime.of(TODAY.minusWeeks(6), LocalTime.of(11,0)));
        final var secondMass = new DatedDraftMass();
        secondMass.setDateTime(LocalDateTime.of(TODAY, LocalTime.of(11,0)));
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
        final var firstMass = new DatedDraftMass();
        firstMass.setDateTime(LocalDateTime.of(TODAY, LocalTime.of(11,0)));
        final var secondMass = new DatedDraftMass();
        secondMass.setDateTime(LocalDateTime.of(TODAY.plusWeeks(1), LocalTime.of(11,0)));
        final var thirdMass = new DatedDraftMass();
        thirdMass.setDateTime(LocalDateTime.of(TODAY.plusWeeks(2), LocalTime.of(11,0)));
        final var lastSchedule = new Schedule(config, List.of(thirdMass));
        assertEquals(2, lastSchedule.getFutureDraftMasses().size());
        assertEquals(TODAY.getDayOfWeek(), lastSchedule.getFutureDraftMasses().get(0).getDateTime().getDayOfWeek());
        assertEquals(TODAY.getDayOfWeek(), lastSchedule.getFutureDraftMasses().get(1).getDateTime().getDayOfWeek());
        final var middleSchedule = new Schedule(config, List.of(secondMass), lastSchedule);
        assertEquals(1, middleSchedule.getFutureDraftMasses().size());
        assertEquals(TODAY.getDayOfWeek(), middleSchedule.getFutureDraftMasses().get(0).getDateTime().getDayOfWeek());
        final var firstSchedule = new Schedule(config, List.of(firstMass), middleSchedule);
        assertEquals(0, firstSchedule.getFutureDraftMasses().size());
    }

}
