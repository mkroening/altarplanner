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
@XmlType(propOrder = {"planningWindow", "config", "masses", "score"})
public class Schedule implements Serializable {

    private Config config;
    private LocalDateInterval planningWindow;
    private List<PlanningMass> masses;
    @PlanningScore
    private HardSoftScore score;

    public static Schedule load(File input) throws FileNotFoundException, UnexpectedElementException, UnknownJAXBException {
        Schedule unmarshalled = JaxbIO.unmarshal(input, Schedule.class);
        unmarshalled.masses.forEach(mass -> mass.getServices().forEach(service -> service.setMass(mass)));
        return unmarshalled;
    }

    public Schedule() {
    }

    public Schedule(Schedule lastSchedule, Collection<DiscreteMass> discreteMassesToPlan, Config config) {
        this.config = config;

        List<PlanningMass> planningMassesToPlan = discreteMassesToPlan.parallelStream()
                .map(PlanningMass::new)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());

        this.planningWindow = LocalDateInterval.of(planningMassesToPlan.get(0).getDate(),
                planningMassesToPlan.get(planningMassesToPlan.size() - 1).getDate());

        LocalDateInterval pastWindow = LocalDateInterval.of(planningWindow.getStart().minusWeeks(1), planningWindow.getStart().minusDays(1));
        List<PlanningMass> pastPlanningMassesToConsider = Optional.ofNullable(lastSchedule)
                .map(schedule -> schedule.getMasses().parallelStream()
                        .filter(planningMass -> pastWindow.contains(planningMass.getDate()))
                        .collect(Collectors.toUnmodifiableList()))
                .orElse(Collections.emptyList());

        LocalDateInterval futureWindow = LocalDateInterval.of(planningWindow.getEnd().plusDays(1), planningWindow.getEnd().plusWeeks(1));
        List<PlanningMass> futurePlanningMassesToConsider = config
                .getDiscreteMassParallelStreamWithin(futureWindow)
                .map(PlanningMass::new)
                .collect(Collectors.toUnmodifiableList());

        this.masses = Stream
                .of(pastPlanningMassesToConsider,
                        planningMassesToPlan,
                        futurePlanningMassesToConsider)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toUnmodifiableList());

        List<Service> services = getServices();
        IntStream.range(0, services.size()).parallel()
                .forEach(value -> services.get(value).setId(value));
    }

    public Map<LocalDate, List<PlanningMass>> getDateMassesMap() {
        return masses.parallelStream().collect(Collectors.groupingByConcurrent(PlanningMass::getDate));
    }

    public int getAvailableServerCountFor(Service service) {
        long count = config.getServers().parallelStream()
                .filter(server -> server.isAvailableFor(service))
                .count();
        return Math.toIntExact(count);
    }

    public int getAvailableServiceCountFor(Server server) {
        long count = masses.parallelStream()
                .flatMap(planningMass -> planningMass.getServices().parallelStream())
                .filter(server::isAvailableFor)
                .count();
        return Math.toIntExact(count);
    }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "serverRange")
    public List<Server> getServers() {
        return config.getServers();
    }

    @PlanningEntityCollectionProperty
    public List<Service> getServices() {
        return masses.parallelStream()
                .flatMap(mass -> mass.getServices().parallelStream())
                .collect(Collectors.toUnmodifiableList());
    }

    @ProblemFactCollectionProperty
    public List<DateOffRequest> getDateOffRequests() {
        final Set<LocalDate> relevantDates = masses.parallelStream()
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
        final Set<LocalDateTime> relevantDateTimes = masses.parallelStream()
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

    @XmlElementWrapper(name = "masses")
    @XmlElement(name = "mass")
    public List<PlanningMass> getMasses() {
        return masses;
    }

    public void setMasses(List<PlanningMass> masses) {
        this.masses = masses;
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
                Objects.equals(masses, schedule.masses) &&
                Objects.equals(getServices(), schedule.getServices()) &&
                Objects.equals(score, schedule.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, planningWindow, masses, getServices(), score);
    }

}
