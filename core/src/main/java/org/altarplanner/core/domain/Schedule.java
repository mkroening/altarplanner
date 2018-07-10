package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.domain.request.*;
import org.altarplanner.core.util.LocalDateInterval;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnexpectedElementException;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.altarplanner.core.xml.jaxb.util.DateSpanXmlAdapter;
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
@XmlType(propOrder = {"planningWindow", "config", "planningMasses", "score"})
public class Schedule implements Serializable {

    private Config config;
    private LocalDateInterval planningWindow;
    private List<PlanningMass> planningMasses;
    @PlanningScore
    private HardSoftScore score;

    public static Schedule load(File input) throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        Schedule unmarshalled = JaxbIO.unmarshal(input, Schedule.class);
        unmarshalled.planningMasses.forEach(mass -> mass.getServices().forEach(service -> service.setMass(mass)));
        return unmarshalled;
    }

    public Schedule() {
    }

    public Schedule(Collection<DiscreteMass> discreteMassesToPlan, Config config) {
        this.config = config;

        final List<PlanningMass> planningMassesToPlan = discreteMassesToPlan.parallelStream()
                .map(PlanningMass::new)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());

        this.planningWindow = LocalDateInterval.of(planningMassesToPlan.get(0).getDate(),
                planningMassesToPlan.get(planningMassesToPlan.size() - 1).getDate());

        final LocalDate futureStart = planningWindow.getEnd().plusDays(1);
        final LocalDate futureEnd = planningWindow.getEnd().plusWeeks(2);
        final List<PlanningMass> futurePlanningMassesToConsider;
        if (!futureStart.isAfter(futureEnd)) {
            futurePlanningMassesToConsider = config
                    .getDiscreteMassParallelStreamWithin(LocalDateInterval.of(futureStart, futureEnd))
                    .map(PlanningMass::new)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            futurePlanningMassesToConsider = Collections.emptyList();
        }

        this.planningMasses = Stream.of(planningMassesToPlan, futurePlanningMassesToConsider)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());

        final List<Service> services = getServices();
        IntStream.range(0, services.size()).parallel()
                .forEach(value -> services.get(value).setId(value));
    }

    public Schedule(Schedule lastSchedule, Collection<DiscreteMass> discreteMassesToPlan, Config config) {
        this.config = config;

        final List<PlanningMass> planningMassesToPlan = discreteMassesToPlan.parallelStream()
                .map(PlanningMass::new)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());

        this.planningWindow = LocalDateInterval.of(planningMassesToPlan.get(0).getDate(),
                planningMassesToPlan.get(planningMassesToPlan.size() - 1).getDate());
        if (planningWindow.getStart().minusWeeks(2).isAfter(lastSchedule.planningWindow.getEnd()))
            throw new IllegalArgumentException("Given last schedule is too old to be relevant");
        final LocalDateInterval pastWindow = LocalDateInterval
                .of(planningWindow.getStart().minusWeeks(2), lastSchedule.planningWindow.getEnd());
        final List<PlanningMass> pastPlanningMassesToConsider = lastSchedule.planningMasses.parallelStream()
                        .filter(planningMass -> pastWindow.contains(planningMass.getDate()))
                        .collect(Collectors.toUnmodifiableList());
        pastPlanningMassesToConsider.forEach(planningMass -> planningMass.setPinned(true));

        final LocalDate futureStart = planningWindow.getEnd().isAfter(lastSchedule.planningWindow.getEnd())
                ? planningWindow.getEnd().plusDays(1) : lastSchedule.planningWindow.getEnd().plusDays(1);
        final LocalDate futureEnd = planningWindow.getEnd().plusWeeks(2);
        final List<PlanningMass> futurePlanningMassesToConsider;
        if (!futureStart.isAfter(futureEnd)) {
            futurePlanningMassesToConsider = config
                    .getDiscreteMassParallelStreamWithin(LocalDateInterval.of(futureStart, futureEnd))
                    .map(PlanningMass::new)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            futurePlanningMassesToConsider = Collections.emptyList();
        }

        this.planningMasses = Stream
                .of(pastPlanningMassesToConsider,
                        planningMassesToPlan,
                        futurePlanningMassesToConsider)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());

        final List<Service> services = getServices();
        IntStream.range(0, services.size()).parallel()
                .forEach(value -> services.get(value).setId(value));
    }

    public int getAvailableServerCountFor(Service service) {
        long count = config.getServers().parallelStream()
                .filter(server -> server.isAvailableFor(service))
                .count();
        return Math.toIntExact(count);
    }

    public int getAvailableServiceCountFor(Server server) {
        long count = planningMasses.parallelStream()
                .flatMap(planningMass -> planningMass.getServices().parallelStream())
                .filter(server::isAvailableFor)
                .count();
        return Math.toIntExact(count);
    }

    private Stream<PlanningMass> getUnpinnedMasses() {
        return planningMasses.stream().filter(planningMass -> !planningMass.isPinned());
    }

    public List<PlanningMass> getMasses() {
        return getUnpinnedMasses().collect(Collectors.toUnmodifiableList());
    }

    public Map<LocalDate, List<PlanningMass>> getDateMassesMap() {
        return getUnpinnedMasses().collect(Collectors.groupingBy(PlanningMass::getDate));
    }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "serverRange")
    public List<Server> getServers() {
        return config.getServers();
    }

    @PlanningEntityCollectionProperty
    public List<Service> getServices() {
        return planningMasses.parallelStream()
                .flatMap(mass -> mass.getServices().parallelStream())
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<DateOffRequest> getDateOffRequests() {
        final Set<LocalDate> relevantDates = planningMasses.parallelStream()
                .map(PlanningMass::getDate)
                .collect(Collectors.toUnmodifiableSet());
        return config.getServers().parallelStream()
                .flatMap(server -> server.getDateOffRequests(relevantDates))
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<ServiceTypeOffRequest> getServiceTypeOffRequests() {
        return config.getServers().parallelStream()
                .flatMap(Server::getServiceTypeOffRequests)
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<DateTimeOnRequest> getDateTimeOnRequests() {
        final Set<LocalDateTime> relevantDateTimes = planningMasses.parallelStream()
                .map(planningMass -> LocalDateTime.of(planningMass.getDate(), planningMass.getTime()))
                .collect(Collectors.toUnmodifiableSet());
        return config.getServers().parallelStream()
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

    @XmlJavaTypeAdapter(DateSpanXmlAdapter.class)
    public LocalDateInterval getPlanningWindow() {
        return planningWindow;
    }

    public void setPlanningWindow(LocalDateInterval planningWindow) {
        this.planningWindow = planningWindow;
    }

    @XmlElementWrapper(name = "planningMasses")
    @XmlElement(name = "planningMass")
    public List<PlanningMass> getPlanningMasses() {
        return planningMasses;
    }

    public void setPlanningMasses(List<PlanningMass> masses) {
        this.planningMasses = masses;
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
                Objects.equals(planningWindow, schedule.planningWindow) &&
                Objects.equals(planningMasses, schedule.planningMasses) &&
                Objects.equals(getServices(), schedule.getServices()) &&
                Objects.equals(score, schedule.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, planningWindow, planningMasses, getServices(), score);
    }

}
