package org.altarplanner.core.domain;

import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.domain.request.*;
import org.altarplanner.core.xml.StrictJAXB;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jaxb.api.score.buildin.hardsoft.HardSoftScoreJaxbXmlAdapter;
import org.threeten.extra.LocalDateRange;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@PlanningSolution
@XmlRootElement
@XmlType(propOrder = {"config", "publishedMasses", "finalDraftMasses", "futureDraftMasses", "feastDays", "score"})
public class Schedule implements Serializable {

    private Config config;
    private List<PlanningMass> publishedMasses;
    private List<PlanningMass> finalDraftMasses;
    private List<PlanningMass> futureDraftMasses;
    private List<LocalDate> feastDays;
    @PlanningScore
    private HardSoftScore score;

    public static Schedule unmarshal(Path input) throws UnmarshalException {
        Schedule unmarshalled = StrictJAXB.unmarshal(input, Schedule.class);
        unmarshalled.getAllMasses().forEach(mass -> mass.getServices().forEach(service -> service.setMass(mass)));
        unmarshalled.setPlanningIds();
        unmarshalled.setPinned();
        return unmarshalled;
    }

    /**
     * Noarg public constructor making the class instantiatable for OptaPlanner and JAXB.
     */
    public Schedule() {
    }

    public Schedule(ScheduleTemplate scheduleTemplate, Config config) {
        this.config = new Config(config);
        this.publishedMasses = List.of();
        this.finalDraftMasses = scheduleTemplate.getPlanningMassTemplates().stream()
                .map(PlanningMass::new)
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        final LocalDateRange futureDraftRange = LocalDateRange.ofClosed(
                getPlanningWindow().getEndInclusive().plusDays(1),
                getPlanningWindow().getEndInclusive().plusWeeks(2)
        );
        this.futureDraftMasses = config
                .getPlanningMassTemplateStreamFromRegularMassesIn(futureDraftRange)
                .map(PlanningMass::new)
                .collect(Collectors.toUnmodifiableList());
        this.feastDays = scheduleTemplate.getFeastDays();
        setPlanningIds();
        setPinned();
        setServiceTypes();
    }

    public Schedule(ScheduleTemplate scheduleTemplate, Schedule lastSchedule, Config config) {
        this(scheduleTemplate, config);
        final LocalDate publishedRelevanceDate = getPlanningWindow().getStart().minusWeeks(2);
        if (publishedRelevanceDate.isAfter(lastSchedule.getPlanningWindow().getEndInclusive()))
            throw new IllegalArgumentException("The given last schedule is too old to be relevant");
        this.publishedMasses = lastSchedule.getPlannedMasses()
                .filter(mass -> !publishedRelevanceDate.isAfter(mass.getDateTime().toLocalDate()))
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        publishedMasses.forEach(planningMass ->
                planningMass.setServices(planningMass.getServices().stream()
                        .filter(service -> config.getServers().contains(service.getServer()))
                        .collect(Collectors.toUnmodifiableList())
                )
        );
        publishedMasses.forEach(mass -> mass.setPinned(true));
        final LocalDate lastPublishedDate = publishedMasses.get(publishedMasses.size() - 1).getDateTime().toLocalDate();
        final LocalDate lastFinalDraftDate = finalDraftMasses.get(finalDraftMasses.size() - 1).getDateTime().toLocalDate();
        if (lastPublishedDate.isAfter(lastFinalDraftDate)) {
            final LocalDate futureRelevanceDate = lastFinalDraftDate.plusWeeks(2);
            if (futureRelevanceDate.isAfter(lastPublishedDate)) {
                final LocalDateRange futureDraftRange = LocalDateRange.ofClosed(
                        lastPublishedDate.plusDays(1),
                        futureRelevanceDate
                );
                this.futureDraftMasses = config
                        .getPlanningMassTemplateStreamFromRegularMassesIn(futureDraftRange)
                        .map(PlanningMass::new)
                        .collect(Collectors.toUnmodifiableList());
            } else {
                this.futureDraftMasses = List.of();
            }
        }
        setPlanningIds();
        setPinned();
        setServiceTypes();
    }

    public void marshal(Path output) {
        StrictJAXB.marshal(this, output);
    }

    private void setPlanningIds() {
        final List<List<? extends AbstractPersistable>> abstractPersistableLists = List.of(getServices(), getServers());
        abstractPersistableLists.forEach(list -> IntStream.range(0, list.size())
                .forEach(index -> list.get(index).setPlanningId(index)));
    }

    private void setPinned() {
        publishedMasses.forEach(mass -> mass.setPinned(true));
        finalDraftMasses.forEach(mass -> mass.setPinned(false));
        futureDraftMasses.forEach(mass -> mass.setPinned(false));
    }

    private void setServiceTypes() {
        final var serviceTypes = getServices().stream()
                .map(Service::getType)
                .collect(Collectors.toUnmodifiableSet());
        getServers().forEach(server -> server.getInabilities().removeIf(Predicate.not(serviceTypes::contains)));
        config.setServiceTypes(serviceTypes.stream().sorted().collect(Collectors.toUnmodifiableList()));
    }

    private Stream<PlanningMass> getAllMasses() {
        return List.of(publishedMasses, finalDraftMasses, futureDraftMasses).stream()
                .flatMap(Collection::stream);
    }

    private Stream<PlanningMass> getPlannedMasses() {
        return List.of(publishedMasses, finalDraftMasses).stream()
                .flatMap(Collection::stream);
    }

    private Stream<PlanningMass> getDraftMasses() {
        return List.of(finalDraftMasses, futureDraftMasses).stream()
                .flatMap(Collection::stream);
    }

    public int getAvailableServerCountFor(Service service) {
        long count = config.getServers().stream()
                .filter(server -> server.isAvailableFor(service))
                .count();
        return Math.toIntExact(count);
    }

    public int getAvailableServiceCountFor(Server server) {
        long count = getDraftMasses()
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
        return config.getServers();
    }

    @PlanningEntityCollectionProperty
    public List<Service> getServices() {
        return getAllMasses()
                .flatMap(mass -> mass.getServices().stream())
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<DateOffRequest> getDateOffRequests() {
        final Set<LocalDate> relevantDates = getDraftMasses()
                .map(PlanningMass::getDateTime)
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.toUnmodifiableSet());
        return config.getServers().stream()
                .flatMap(server -> server.getDateOffRequests(relevantDates, Set.copyOf(feastDays)))
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<ServiceTypeOffRequest> getServiceTypeOffRequests() {
        return config.getServers().stream()
                .flatMap(Server::getServiceTypeOffRequests)
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<DateTimeOnRequest> getDateTimeOnRequests() {
        final Set<LocalDateTime> relevantDateTimes = getDraftMasses()
                .map(PlanningMass::getDateTime)
                .collect(Collectors.toUnmodifiableSet());
        return config.getServers().stream()
                .flatMap(server -> server.getDateTimeOnRequests(relevantDateTimes))
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<PairRequest> getPairRequests() {
        return config.getPairs();
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @XmlElementWrapper(name = "publishedMasses")
    @XmlElement(name = "planningMass")
    public List<PlanningMass> getPublishedMasses() {
        return publishedMasses;
    }

    public void setPublishedMasses(List<PlanningMass> publishedMasses) {
        this.publishedMasses = publishedMasses;
    }

    @XmlElementWrapper(name = "finalDraftMasses")
    @XmlElement(name = "planningMass")
    public List<PlanningMass> getFinalDraftMasses() {
        return finalDraftMasses;
    }

    public void setFinalDraftMasses(List<PlanningMass> finalDraftMasses) {
        this.finalDraftMasses = finalDraftMasses;
    }

    @XmlElementWrapper(name = "futureDraftMasses")
    @XmlElement(name = "planningMass")
    public List<PlanningMass> getFutureDraftMasses() {
        return futureDraftMasses;
    }

    public void setFutureDraftMasses(List<PlanningMass> futureDraftMasses) {
        this.futureDraftMasses = futureDraftMasses;
    }

    @XmlList
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public List<LocalDate> getFeastDays() {
        return feastDays;
    }

    public void setFeastDays(List<LocalDate> feastDays) {
        this.feastDays = feastDays;
    }

    @XmlJavaTypeAdapter(HardSoftScoreJaxbXmlAdapter.class)
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(config, schedule.config) &&
                Objects.equals(publishedMasses, schedule.publishedMasses) &&
                Objects.equals(finalDraftMasses, schedule.finalDraftMasses) &&
                Objects.equals(futureDraftMasses, schedule.futureDraftMasses) &&
                Objects.equals(score, schedule.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, publishedMasses, finalDraftMasses, futureDraftMasses, score);
    }

}
