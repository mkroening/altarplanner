package org.altarplanner.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.planning.Server;
import org.altarplanner.core.domain.state.Config;
import org.altarplanner.core.domain.state.Schedule;
import org.altarplanner.core.domain.state.ScheduleTemplate;
import org.junit.jupiter.api.Test;
import org.threeten.extra.LocalDateRange;

class ScheduleTests {

  private static final LocalDate LAST_MONDAY =
      LocalDate.now()
          .plusDays(DayOfWeek.MONDAY.getValue() - LocalDate.now().getDayOfWeek().getValue());

  @Test
  void constructorPublishedRelevanceTest() {
    final var config = new Config();
    final var firstMass = new PlanningMassTemplate();
    firstMass.setDateTime(LocalDateTime.of(LAST_MONDAY.minusWeeks(2), LocalTime.of(11, 0)));
    final var secondMass = new PlanningMassTemplate();
    secondMass.setDateTime(LocalDateTime.of(LAST_MONDAY, LocalTime.of(11, 0)));
    final var thirdMass = new PlanningMassTemplate();
    thirdMass.setDateTime(LocalDateTime.of(LAST_MONDAY.plusWeeks(2), LocalTime.of(11, 0)));
    final var publishedSchedule =
        new Schedule(new ScheduleTemplate(List.of(firstMass, secondMass)), config);
    final var schedule =
        new Schedule(new ScheduleTemplate(List.of(thirdMass)), publishedSchedule, config);
    assertEquals(1, schedule.getPublishedMasses().size());
    assertSame(
        publishedSchedule.getFinalDraftMasses().get(1), schedule.getPublishedMasses().get(0));
  }

  @Test
  void constructorPublishedFutureTest() {
    final var config = new Config();
    final var firstMass = new PlanningMassTemplate();
    firstMass.setDateTime(LocalDateTime.of(LAST_MONDAY.minusWeeks(6), LocalTime.of(11, 0)));
    final var secondMass = new PlanningMassTemplate();
    secondMass.setDateTime(LocalDateTime.of(LAST_MONDAY, LocalTime.of(11, 0)));
    final var publishedSchedule = new Schedule(new ScheduleTemplate(List.of(secondMass)), config);
    final var schedule =
        new Schedule(new ScheduleTemplate(List.of(firstMass)), publishedSchedule, config);
    assertEquals(1, schedule.getPublishedMasses().size());
    assertSame(
        publishedSchedule.getFinalDraftMasses().get(0), schedule.getPublishedMasses().get(0));
  }

  @Test
  void constructorFutureDraftTest() {
    final var config = new Config();
    final var regularMass = new RegularMass();
    regularMass.setDay(DayOfWeek.MONDAY);
    config.setRegularMasses(List.of(regularMass));
    final var firstMass = new PlanningMassTemplate();
    firstMass.setDateTime(LocalDateTime.of(LAST_MONDAY, LocalTime.of(11, 0)));
    final var secondMass = new PlanningMassTemplate();
    secondMass.setDateTime(LocalDateTime.of(LAST_MONDAY.plusWeeks(1), LocalTime.of(11, 0)));
    final var thirdMass = new PlanningMassTemplate();
    thirdMass.setDateTime(LocalDateTime.of(LAST_MONDAY.plusWeeks(2), LocalTime.of(11, 0)));
    final var lastSchedule = new Schedule(new ScheduleTemplate(List.of(thirdMass)), config);
    assertEquals(2, lastSchedule.getFutureDraftMasses().size());
    assertEquals(
        LAST_MONDAY.getDayOfWeek(),
        lastSchedule.getFutureDraftMasses().get(0).getDateTime().getDayOfWeek());
    assertEquals(
        LAST_MONDAY.getDayOfWeek(),
        lastSchedule.getFutureDraftMasses().get(1).getDateTime().getDayOfWeek());
    final var middleSchedule =
        new Schedule(new ScheduleTemplate(List.of(secondMass)), lastSchedule, config);
    assertEquals(1, middleSchedule.getFutureDraftMasses().size());
    assertEquals(
        LAST_MONDAY.getDayOfWeek(),
        middleSchedule.getFutureDraftMasses().get(0).getDateTime().getDayOfWeek());
    final var firstSchedule =
        new Schedule(new ScheduleTemplate(List.of(firstMass)), middleSchedule, config);
    assertEquals(0, firstSchedule.getFutureDraftMasses().size());
  }

  @Test
  void getDateOffRequestTest() {
    final var config = new Config();
    final var server = new Server();
    server.setWeeklyAbsences(
        List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY));
    server.setAbsences(
        List.of(LocalDateRange.ofClosed(LAST_MONDAY.plusDays(2), LAST_MONDAY.plusDays(5))));
    config.setServers(List.of(server));
    final var planningMassTemplates =
        IntStream.of(1, 3, 5, 7)
            .mapToObj(
                dayOfWeek -> {
                  final var planningMassTemplate = new PlanningMassTemplate();
                  planningMassTemplate.setDateTime(
                      LocalDateTime.of(LAST_MONDAY.plusDays(dayOfWeek - 1), LocalTime.MIDNIGHT));
                  return planningMassTemplate;
                })
            .collect(Collectors.toUnmodifiableList());

    final var normalSchedule = new Schedule(new ScheduleTemplate(planningMassTemplates), config);
    final var normalDateOffRequests = normalSchedule.getDateOffRequests();
    assertIterableEquals(
        List.of(LAST_MONDAY, LAST_MONDAY.plusDays(2), LAST_MONDAY.plusDays(4)),
        normalDateOffRequests.stream()
            .map(AbstractMap.SimpleImmutableEntry::getValue)
            .sorted()
            .collect(Collectors.toUnmodifiableList()),
        "DateOffRequests are not correct");

    final var feastDays =
        IntStream.rangeClosed(1, 7)
            .mapToObj(dayOfWeek -> LAST_MONDAY.plusDays(dayOfWeek - 1))
            .collect(Collectors.toUnmodifiableList());
    final var feastDaySchedule =
        new Schedule(new ScheduleTemplate(planningMassTemplates, feastDays), config);
    final var feastDayDateOffRequests = feastDaySchedule.getDateOffRequests();
    assertIterableEquals(
        List.of(LAST_MONDAY.plusDays(2), LAST_MONDAY.plusDays(4)),
        feastDayDateOffRequests.stream()
            .map(AbstractMap.SimpleImmutableEntry::getValue)
            .sorted()
            .collect(Collectors.toUnmodifiableList()),
        "feast day DateOffRequests are not correct");
  }
}
