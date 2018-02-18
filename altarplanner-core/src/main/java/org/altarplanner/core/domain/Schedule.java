package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.PlanningMass;
import org.altarplanner.core.domain.request.*;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@PlanningSolution
public class Schedule {

    private Set<Consumer<String>> stringConsumerSet = new HashSet<>();
    @Getter private final DateSpan planningWindow;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "serverRange")
    @Getter private final List<Server> servers;
    @Getter private final List<PlanningMass> masses;
    @PlanningScore
    @Getter @Setter private HardSoftScore score;

    public Schedule() {
        this.planningWindow = new DateSpan();
        this.servers = Collections.emptyList();
        this.masses = Collections.emptyList();
    }

    public Schedule(Schedule lastSchedule, Collection<DiscreteMass> discreteMassesToPlan, Config config) {
        List<PlanningMass> planningMassesToPlan = discreteMassesToPlan.parallelStream()
                .map(PlanningMass::new)
                .sorted(Comparator.comparing(PlanningMass::getDate))
                .collect(Collectors.toList());

        this.planningWindow = new DateSpan(planningMassesToPlan.get(0).getDate(),
                planningMassesToPlan.get(planningMassesToPlan.size() - 1).getDate());

        DateSpan pastWindow = new DateSpan(planningWindow.getStart().minusWeeks(1), planningWindow.getStart().minusDays(1));
        List<PlanningMass> pastPlanningMassesToConsider = Optional.ofNullable(lastSchedule)
                .map(schedule -> schedule.getMasses().parallelStream()
                        .filter(planningMass -> pastWindow.contains(planningMass.getDate()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        DateSpan futureWindow = new DateSpan(planningWindow.getEnd().plusDays(1), planningWindow.getEnd().plusWeeks(1));
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

        this.servers = config.getServers();
    }

    public int getAvailableServerCountFor(Service service) {
        long count = servers.parallelStream()
                .filter(server -> server.isAvailableFor(service))
                .count();
        return Math.toIntExact(count);
    }

    public Schedule solve() {
        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource("org/altarplanner/core/solver/solverConfig.xml");
        Solver<Schedule> solver = solverFactory.buildSolver();
        solver.addEventListener(bestSolutionChangedEvent -> {
            String newBestScore = bestSolutionChangedEvent.getNewBestScore().toShortString();
            LoggerFactory.getLogger(getClass()).info("New best score ({})", newBestScore);
            stringConsumerSet.parallelStream().forEach(stringConsumer -> stringConsumer.accept(newBestScore));
        });
        Schedule bestSolution = solver.solve(this);

        // Log constraint break down
        try (ScoreDirector<Schedule> guiScoreDirector = solver.getScoreDirectorFactory().buildScoreDirector()) {
            guiScoreDirector.setWorkingSolution(bestSolution);
            LoggerFactory.getLogger(getClass()).debug("Constraint break down: score ({})", guiScoreDirector.calculateScore());

            guiScoreDirector.getConstraintMatchTotals().forEach(constraintMatchTotal -> {
                LoggerFactory.getLogger(getClass()).debug(
                        "Constraint: {} ({})",
                        constraintMatchTotal.getConstraintName(),
                        constraintMatchTotal.getScoreTotal());

                constraintMatchTotal.getConstraintMatchSet().forEach(constraintMatch ->
                        LoggerFactory.getLogger(getClass()).debug(
                                "Match ({}): {}",
                                constraintMatch.getConstraintName(),
                                constraintMatch.getJustificationList())
                );
            });
        }

        return bestSolution;
    }

    @PlanningEntityCollectionProperty
    public List<Service> getServices() {
        return masses.parallelStream()
                .flatMap(mass -> mass.getServices().parallelStream())
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<DateOffRequest> getDateOffRequests() {
        return servers.parallelStream()
                .flatMap(Server::getDateOffRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<DayOffRequest> getDayOffRequests() {
        return servers.parallelStream()
                .flatMap(Server::getDayOffRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<ServiceTypeOffRequest> getServiceTypeOffRequests() {
        return servers.parallelStream()
                .flatMap(Server::getServiceTypeOffRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<DateTimeOnRequest> getDateTimeOnRequests() {
        return servers.parallelStream()
                .flatMap(Server::getDateTimeOnRequestParallelStream)
                .collect(Collectors.toList());
    }

    @ProblemFactCollectionProperty
    public List<PairRequest> getPairRequests() {
        return servers.parallelStream()
                .flatMap(Server::getPairRequestParallelStream)
                .collect(Collectors.toList());
    }

    public void addNewBestScoreStringConsumer(Consumer<String> newBestScoreStringConsumer) {
        stringConsumerSet.add(newBestScoreStringConsumer);
    }

    public void removeNewBestScoreStringConsumer(Consumer<String> newBestScoreStringConsumer) {
        stringConsumerSet.remove(newBestScoreStringConsumer);
    }

}
