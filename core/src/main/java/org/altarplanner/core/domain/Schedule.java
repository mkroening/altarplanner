package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.domain.request.*;
import org.altarplanner.core.util.LocalDateInterval;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnexpectedElementException;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jaxb.api.score.buildin.hardsoft.HardSoftScoreJaxbXmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@PlanningSolution
@XmlRootElement
@XmlType(propOrder = {"config", "publishedMasses", "finalDraftMasses", "futureDraftMasses", "score"})
public class Schedule implements Serializable {

    private Config config;
    private List<PlanningMass> publishedMasses;
    private List<PlanningMass> finalDraftMasses;
    private List<PlanningMass> futureDraftMasses;
    @PlanningScore
    private HardSoftScore score;

    public static Schedule load(File input) throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        Schedule unmarshalled = JaxbIO.unmarshal(input, Schedule.class);
        unmarshalled.getAllMasses().forEach(mass -> mass.getServices().forEach(service -> service.setMass(mass)));
        unmarshalled.setPlanningIds();
        unmarshalled.setPinned();
        return unmarshalled;
    }

    public Schedule() {
    }

    public Schedule(Config config, Collection<DiscreteMass> masses) {
        this.config = config;
        this.publishedMasses = List.of();
        this.finalDraftMasses = masses.stream()
                .map(PlanningMass::new)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());
        final LocalDateInterval futureDraftInterval = LocalDateInterval.of(
                getPlanningWindow().getEnd().plusDays(1),
                getPlanningWindow().getEnd().plusWeeks(2)
        );
        this.futureDraftMasses = config
                .getDiscreteMassParallelStreamWithin(futureDraftInterval)
                .map(PlanningMass::new)
                .collect(Collectors.toUnmodifiableList());
        setPlanningIds();
        setPinned();
    }

    public Schedule(Config config, Collection<DiscreteMass> masses, Schedule lastSchedule) {
        this(config, masses);
        final LocalDate publishedRelevanceDate = getPlanningWindow().getStart().minusWeeks(2);
        if (publishedRelevanceDate.isAfter(lastSchedule.getPlanningWindow().getEnd()))
            throw new IllegalArgumentException("The given last schedule is too old to be relevant");
        this.publishedMasses = lastSchedule.getPlannedMasses()
                .filter(mass -> !publishedRelevanceDate.isAfter(mass.getDate()))
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());
        publishedMasses.forEach(planningMass ->
                planningMass.setServices(planningMass.getServices().stream()
                        .filter(service -> config.getServers().contains(service.getServer()))
                        .collect(Collectors.toUnmodifiableList())
                )
        );
        publishedMasses.forEach(mass -> mass.setPinned(true));
        final LocalDate lastPublishedDate = publishedMasses.get(publishedMasses.size() - 1).getDate();
        final LocalDate lastFinalDraftDate = finalDraftMasses.get(finalDraftMasses.size() - 1).getDate();
        if (lastPublishedDate.isAfter(lastFinalDraftDate)) {
            final LocalDate futureRelevanceDate = lastFinalDraftDate.plusWeeks(2);
            if (futureRelevanceDate.isAfter(lastPublishedDate)) {
                final LocalDateInterval futureDraftInterval = LocalDateInterval.of(
                        lastPublishedDate.plusDays(1),
                        futureRelevanceDate
                );
                this.futureDraftMasses = config
                        .getDiscreteMassParallelStreamWithin(futureDraftInterval)
                        .map(PlanningMass::new)
                        .collect(Collectors.toUnmodifiableList());
            } else {
                this.futureDraftMasses = List.of();
            }
        }
        setPlanningIds();
        setPinned();
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

    public LocalDateInterval getPlanningWindow() {
        return LocalDateInterval.of(finalDraftMasses.get(0).getDate(), finalDraftMasses.get(finalDraftMasses.size() - 1).getDate());
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
                .map(PlanningMass::getDate)
                .collect(Collectors.toUnmodifiableSet());
        return config.getServers().stream()
                .flatMap(server -> server.getDateOffRequests(relevantDates))
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
                .map(planningMass -> LocalDateTime.of(planningMass.getDate(), planningMass.getTime()))
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
    @XmlElement(name = "mass")
    public List<PlanningMass> getPublishedMasses() {
        return publishedMasses;
    }

    public void setPublishedMasses(List<PlanningMass> publishedMasses) {
        this.publishedMasses = publishedMasses;
    }

    @XmlElementWrapper(name = "finalDraftMasses")
    @XmlElement(name = "mass")
    public List<PlanningMass> getFinalDraftMasses() {
        return finalDraftMasses;
    }

    public void setFinalDraftMasses(List<PlanningMass> finalDraftMasses) {
        this.finalDraftMasses = finalDraftMasses;
    }

    @XmlElementWrapper(name = "futureDraftMasses")
    @XmlElement(name = "mass")
    public List<PlanningMass> getFutureDraftMasses() {
        return futureDraftMasses;
    }

    public void setFutureDraftMasses(List<PlanningMass> futureDraftMasses) {
        this.futureDraftMasses = futureDraftMasses;
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
                Objects.equals(getServices(), schedule.getServices()) &&
                Objects.equals(score, schedule.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, publishedMasses, finalDraftMasses, futureDraftMasses, getServices(), score);
    }

}
