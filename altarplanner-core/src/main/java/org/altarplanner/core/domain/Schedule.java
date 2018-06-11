package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.domain.request.*;
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
import java.io.Serializable;
import java.time.LocalDate;
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

    public Schedule() {
    }

    public Schedule(Schedule lastSchedule, Collection<DiscreteMass> discreteMassesToPlan, Config config) {
        this.config = config;

        List<PlanningMass> planningMassesToPlan = discreteMassesToPlan.parallelStream()
                .map(PlanningMass::new)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toList());

        this.planningWindow = LocalDateInterval.of(planningMassesToPlan.get(0).getDate(),
                planningMassesToPlan.get(planningMassesToPlan.size() - 1).getDate());

        LocalDateInterval pastWindow = LocalDateInterval.of(planningWindow.getStart().minusWeeks(1), planningWindow.getStart().minusDays(1));
        List<PlanningMass> pastPlanningMassesToConsider = Optional.ofNullable(lastSchedule)
                .map(schedule -> schedule.getMasses().parallelStream()
                        .filter(planningMass -> pastWindow.contains(planningMass.getDate()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        LocalDateInterval futureWindow = LocalDateInterval.of(planningWindow.getEnd().plusDays(1), planningWindow.getEnd().plusWeeks(1));
        List<PlanningMass> futurePlanningMassesToConsider = config
                .getDiscreteMassParallelStreamWithin(futureWindow)
                .map(PlanningMass::new)
                .collect(Collectors.toList());

        this.masses = Stream
                .of(pastPlanningMassesToConsider,
                        planningMassesToPlan,
                        futurePlanningMassesToConsider)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toList());

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

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "serverRange")
    public List<Server> getServers() {
        return config.getServers();
    }

    @PlanningEntityCollectionProperty
    public List<Service> getServices() {
        return masses.parallelStream()
                .flatMap(mass -> mass.getServices().parallelStream())
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<DateOffRequest> getDateOffRequests() {
        return config.getServers().parallelStream()
                .flatMap(Server::getDateOffRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<DayOffRequest> getDayOffRequests() {
        return config.getServers().parallelStream()
                .flatMap(Server::getDayOffRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<ServiceTypeOffRequest> getServiceTypeOffRequests() {
        return config.getServers().parallelStream()
                .flatMap(Server::getServiceTypeOffRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<DateTimeOnRequest> getDateTimeOnRequests() {
        return config.getServers().parallelStream()
                .flatMap(Server::getDateTimeOnRequestParallelStream)
                .collect(Collectors.toList());
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

}
