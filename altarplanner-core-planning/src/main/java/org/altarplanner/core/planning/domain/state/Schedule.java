package org.altarplanner.core.planning.domain.state;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.altarplanner.core.planning.domain.mass.PlanningMass;
import org.altarplanner.core.planning.domain.planning.AbstractPersistable;
import org.altarplanner.core.planning.domain.planning.Server;
import org.altarplanner.core.planning.domain.planning.Service;
import org.altarplanner.core.planning.domain.request.DateOffRequest;
import org.altarplanner.core.planning.domain.request.DateTimeOnRequest;
import org.altarplanner.core.planning.domain.request.PairRequest;
import org.altarplanner.core.planning.domain.request.ServiceTypeOffRequest;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.threeten.extra.LocalDateRange;

@PlanningSolution
public class Schedule extends ServerAware implements FeastDayAware, Serializable {

  private List<PlanningMass> publishedMasses;
  private List<PlanningMass> finalDraftMasses;
  private List<PlanningMass> futureDraftMasses;
  private List<LocalDate> feastDays;
  @PlanningScore private HardSoftScore score;

  /** Noarg public constructor making the class instantiatable for OptaPlanner and JAXB. */
  public Schedule() {}

  public Schedule(ScheduleTemplate scheduleTemplate, Config config) {
    super(config);
    this.publishedMasses = List.of();
    this.finalDraftMasses =
        scheduleTemplate.getPlanningMassTemplates().stream()
            .map(PlanningMass::new)
            .sorted()
            .collect(Collectors.toUnmodifiableList());
    final LocalDateRange futureDraftRange =
        LocalDateRange.ofClosed(
            getPlanningWindow().getEndInclusive().plusDays(1),
            getPlanningWindow().getEndInclusive().plusWeeks(2));
    this.futureDraftMasses =
        config
            .getPlanningMassTemplateStreamFromRegularMassesIn(futureDraftRange)
            .map(PlanningMass::new)
            .collect(Collectors.toUnmodifiableList());
    this.feastDays = scheduleTemplate.getFeastDays();
    updatePlanningIds();
    updateMassIsPinned();
    setServiceTypes();
  }

  public Schedule(ScheduleTemplate scheduleTemplate, Schedule lastSchedule, Config config) {
    this(scheduleTemplate, config);
    final LocalDate publishedRelevanceDate = getPlanningWindow().getStart().minusWeeks(2);
    if (publishedRelevanceDate.isAfter(lastSchedule.getPlanningWindow().getEndInclusive())) {
      throw new IllegalArgumentException("The given last schedule is too old to be relevant");
    }
    this.publishedMasses =
        lastSchedule
            .getPlannedMasses()
            .filter(mass -> !publishedRelevanceDate.isAfter(mass.getDateTime().toLocalDate()))
            .sorted()
            .collect(Collectors.toUnmodifiableList());
    publishedMasses.forEach(
        planningMass ->
            planningMass.setServices(
                planningMass.getServices().stream()
                    .filter(service -> config.getServers().contains(service.getServer()))
                    .collect(Collectors.toUnmodifiableList())));
    publishedMasses.forEach(mass -> mass.setPinned(true));
    final LocalDate lastPublishedDate =
        publishedMasses.get(publishedMasses.size() - 1).getDateTime().toLocalDate();
    final LocalDate lastFinalDraftDate =
        finalDraftMasses.get(finalDraftMasses.size() - 1).getDateTime().toLocalDate();
    if (lastPublishedDate.isAfter(lastFinalDraftDate)) {
      final LocalDate futureRelevanceDate = lastFinalDraftDate.plusWeeks(2);
      if (futureRelevanceDate.isAfter(lastPublishedDate)) {
        final LocalDateRange futureDraftRange =
            LocalDateRange.ofClosed(lastPublishedDate.plusDays(1), futureRelevanceDate);
        this.futureDraftMasses =
            config
                .getPlanningMassTemplateStreamFromRegularMassesIn(futureDraftRange)
                .map(PlanningMass::new)
                .collect(Collectors.toUnmodifiableList());
      } else {
        this.futureDraftMasses = List.of();
      }
    }
    updatePlanningIds();
    updateMassIsPinned();
    setServiceTypes();
  }

  public void updateServiceMassReferences() {
    getAllMasses().forEach(mass -> mass.getServices().forEach(service -> service.setMass(mass)));
  }

  public void updatePlanningIds() {
    final List<List<? extends AbstractPersistable>> abstractPersistableLists =
        List.of(getServices(), getServers());
    abstractPersistableLists.forEach(
        list ->
            IntStream.range(0, list.size()).forEach(index -> list.get(index).setPlanningId(index)));
  }

  public void updateMassIsPinned() {
    publishedMasses.forEach(mass -> mass.setPinned(true));
    finalDraftMasses.forEach(mass -> mass.setPinned(false));
    futureDraftMasses.forEach(mass -> mass.setPinned(false));
  }

  private void setServiceTypes() {
    final var serviceTypes =
        getServices().stream().map(Service::getType).collect(Collectors.toUnmodifiableSet());
    getServers()
        .forEach(server -> server.setInabilities(
            server.getInabilities().stream()
              .filter(serviceTypes::contains)
              .collect(Collectors.toUnmodifiableList())
          ));
    setServiceTypes(serviceTypes.stream().sorted().collect(Collectors.toUnmodifiableList()));
  }

  private Stream<PlanningMass> getAllMasses() {
    return List.of(publishedMasses, finalDraftMasses, futureDraftMasses).stream()
        .flatMap(Collection::stream);
  }

  private Stream<PlanningMass> getPlannedMasses() {
    return List.of(publishedMasses, finalDraftMasses).stream().flatMap(Collection::stream);
  }

  private Stream<PlanningMass> getDraftMasses() {
    return List.of(finalDraftMasses, futureDraftMasses).stream().flatMap(Collection::stream);
  }

  public int getAvailableServerCountFor(Service service) {
    long count = getServers().stream().filter(server -> server.isAvailableFor(service)).count();
    return Math.toIntExact(count);
  }

  public int getAvailableServiceCountFor(Server server) {
    long count =
        getDraftMasses()
            .flatMap(planningMass -> planningMass.getServices().stream())
            .filter(server::isAvailableFor)
            .count();
    return Math.toIntExact(count);
  }

  public LocalDateRange getPlanningWindow() {
    final var start = Collections.min(finalDraftMasses).getDateTime().toLocalDate();
    final var end = Collections.max(finalDraftMasses).getDateTime().toLocalDate();
    return LocalDateRange.ofClosed(start, end);
  }

  @ProblemFactCollectionProperty
  @ValueRangeProvider(id = "serverRange")
  public List<Server> getServers() {
    return super.getServers();
  }

  @PlanningEntityCollectionProperty
  public List<Service> getServices() {
    return getAllMasses()
        .flatMap(mass -> mass.getServices().stream())
        .collect(Collectors.toUnmodifiableList());
  }

  @ProblemFactCollectionProperty
  public List<DateOffRequest> getDateOffRequests() {
    final Set<LocalDate> relevantDates =
        getDraftMasses()
            .map(PlanningMass::getDateTime)
            .map(LocalDateTime::toLocalDate)
            .collect(Collectors.toUnmodifiableSet());
    return getServers().stream()
        .flatMap(server -> server.getDateOffRequests(relevantDates, Set.copyOf(feastDays)))
        .collect(Collectors.toUnmodifiableList());
  }

  @ProblemFactCollectionProperty
  public List<ServiceTypeOffRequest> getServiceTypeOffRequests() {
    return getServers().stream()
        .flatMap(Server::getServiceTypeOffRequests)
        .collect(Collectors.toUnmodifiableList());
  }

  @ProblemFactCollectionProperty
  public List<DateTimeOnRequest> getDateTimeOnRequests() {
    final Set<LocalDateTime> relevantDateTimes =
        getDraftMasses().map(PlanningMass::getDateTime).collect(Collectors.toUnmodifiableSet());
    return getServers().stream()
        .flatMap(server -> server.getDateTimeOnRequests(relevantDateTimes))
        .collect(Collectors.toUnmodifiableList());
  }

  @ProblemFactCollectionProperty
  public List<PairRequest> getPairs() {
    return super.getPairs();
  }

  public List<PlanningMass> getPublishedMasses() {
    return publishedMasses;
  }

  public void setPublishedMasses(List<PlanningMass> publishedMasses) {
    this.publishedMasses = publishedMasses;
  }

  public List<PlanningMass> getFinalDraftMasses() {
    return finalDraftMasses;
  }

  public void setFinalDraftMasses(List<PlanningMass> finalDraftMasses) {
    this.finalDraftMasses = finalDraftMasses;
  }

  public List<PlanningMass> getFutureDraftMasses() {
    return futureDraftMasses;
  }

  public void setFutureDraftMasses(List<PlanningMass> futureDraftMasses) {
    this.futureDraftMasses = futureDraftMasses;
  }

  public List<LocalDate> getFeastDays() {
    return feastDays;
  }

  public void setFeastDays(List<LocalDate> feastDays) {
    this.feastDays = feastDays;
  }

  public HardSoftScore getScore() {
    return score;
  }

  public void setScore(HardSoftScore score) {
    this.score = score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Schedule schedule = (Schedule) o;
    return publishedMasses.equals(schedule.publishedMasses)
        && finalDraftMasses.equals(schedule.finalDraftMasses)
        && futureDraftMasses.equals(schedule.futureDraftMasses)
        && feastDays.equals(schedule.feastDays)
        && score.equals(schedule.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), publishedMasses, finalDraftMasses, futureDraftMasses, feastDays, score);
  }
}
