package org.altarplanner.core.solver;

import org.altarplanner.core.domain.*;
import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.domain.request.PairRequest;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;
import org.threeten.extra.LocalDateRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ScoreConstraintTests {

    private HardSoftScoreVerifier<Schedule> scoreVerifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource("org/altarplanner/core/solver/solverConfig.xml"));

    private final static int massCount = 10;

    private List<PlanningMassTemplate> generatePlanningMassTemplates(Config config, boolean subsequentMasses, boolean subsequentServiceTypes) {
        return IntStream.range(0, massCount)
                .mapToObj(value -> {
                    PlanningMassTemplate planningMassTemplate = new PlanningMassTemplate();
                    planningMassTemplate.setAnnotation(Integer.toString(value));
                    planningMassTemplate.setDateTime(LocalDateTime.of(subsequentMasses ? LocalDate.now().plusDays(value) : LocalDate.now(), LocalTime.of(11,0)));
                    planningMassTemplate.setServiceTypeCounts(Map.of(config.getServiceTypes().get(subsequentServiceTypes ? value : 0), 1));
                    return planningMassTemplate;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Test
    void oneServicePerDay() {
        final String constraintName = "oneServicePerDay";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServiceTypes().add(new ServiceType());

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, false, false);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            int expectedWeight = -value * (value + 1) / 2; // equal to -IntStream.range(0, value + 1).sum()
            scoreVerifier.assertHardWeight(constraintName, expectedWeight, schedule);
        });
    }

    @Test
    void notEnoughExp() {
        final String constraintName = "notEnoughExp";

        Config config = new Config();

        config.setServers(
                IntStream.range(0, massCount)
                        .mapToObj(value -> {
                            Server server = new Server();
                            server.setYear(Year.now().getValue() - value);
                            return server;
                        })
                        .collect(Collectors.toUnmodifiableList())
        );

        config.setServiceTypes(
                IntStream.range(0, massCount)
                        .mapToObj(value -> {
                            ServiceType serviceType = new ServiceType();
                            serviceType.setMaxYear(Year.now().getValue() - value);
                            return serviceType;
                        })
                        .collect(Collectors.toUnmodifiableList())
        );

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, true);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(value));
            scoreVerifier.assertHardWeight(constraintName, 0, schedule);
            schedule.getServices().get(value).setServer(schedule.getServers().get(massCount - 1 - value));
            scoreVerifier.assertHardWeight(constraintName, massCount - 1 - value < value ? -1 : 0, schedule);
            schedule.getServices().get(value).setServer(null);
        });
    }

    @Test
    void dateOffRequest() {
        final String constraintName = "dateOffRequest";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServers().get(0).getAbsences().add(LocalDateRange.ofClosed(LocalDate.now(), LocalDate.now().plusDays(massCount / 2)));
        config.getServiceTypes().add(new ServiceType());

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, false);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            scoreVerifier.assertHardWeight(constraintName, value <= massCount / 2 ? -1 : 0, schedule);
            schedule.getServices().get(value).setServer(null);
        });
    }

    @Test
    void dayOffRequest() {
        final String constraintName = "dateOffRequest";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServers().get(0).getWeeklyAbsences()
                .addAll(IntStream.range(0, 4)
                        .mapToObj(value -> LocalDate.now().plusDays(2 * value).getDayOfWeek())
                        .collect(Collectors.toUnmodifiableList()));
        config.getServiceTypes().add(new ServiceType());

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, false);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            scoreVerifier.assertHardWeight(constraintName, value % 7 % 2 == 0 ? -1 : 0, schedule);
            schedule.getServices().get(value).setServer(null);
        });
    }

    @Test
    void serviceTypeOffRequest() {
        final String constraintName = "serviceTypeOffRequest";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServiceTypes()
                .addAll(IntStream.range(0, massCount)
                        .mapToObj(value -> {
                            ServiceType serviceType = new ServiceType();
                            serviceType.setName(String.valueOf(value));
                            return serviceType;
                        })
                        .collect(Collectors.toUnmodifiableList()));
        config.getServers().get(0).getInabilities()
                .addAll(IntStream.range(0, massCount / 2)
                        .mapToObj(value -> config.getServiceTypes().get(2 * value))
                        .collect(Collectors.toUnmodifiableList()));

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, true);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            scoreVerifier.assertHardWeight(constraintName, value % 2 == 0 ? -1 : 0, schedule);
            schedule.getServices().get(value).setServer(null);
        });
    }

    @Test
    void dateTimeOnRequest() {
        final String constraintName = "dateTimeOnRequest";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServers().get(0).getDateTimeOnWishes()
                .addAll(IntStream.range(0, massCount / 2)
                        .mapToObj(value -> LocalDateTime.of(LocalDate.now().plusDays(2 * value), LocalTime.of(11, 0)))
                        .collect(Collectors.toUnmodifiableList()));
        config.getServiceTypes().add(new ServiceType());

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, false);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        schedule.getServices().forEach(service -> service.setServer(schedule.getServers().get(0)));

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(null);
            scoreVerifier.assertHardWeight(constraintName, value % 2 == 0 ? -1 : 0, schedule);
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
        });
    }

    @Test
    void pairRequest() {
        final String constraintName = "pairRequest";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServers().add(new Server());
        config.getServers().get(0).setSurname("First");
        config.getServers().get(1).setSurname("Second");
        config.getPairs().add(new PairRequest(config.getServers().get(0), config.getServers().get(1)));
        config.getServiceTypes().add(new ServiceType());

        PlanningMassTemplate planningMassTemplate = new PlanningMassTemplate();
        planningMassTemplate.setServiceTypeCounts(Map.of(config.getServiceTypes().get(0), 2));

        Schedule schedule = new Schedule(config, List.of(planningMassTemplate));

        scoreVerifier.assertSoftWeight(constraintName, 0, schedule);

        schedule.getServices().get(0).setServer(schedule.getServers().get(0));
        scoreVerifier.assertSoftWeight(constraintName, -10, schedule);

        schedule.getServices().get(1).setServer(schedule.getServers().get(1));
        scoreVerifier.assertSoftWeight(constraintName, 0, schedule);
    }

    // TODO: refine (no interval with services in between are being tested)
    @Test
    void maximizeIntervals() {
        final String constraintName = "maximizeIntervals";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServiceTypes().add(new ServiceType());

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, false);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertSoftWeight(constraintName, 0, schedule);

        schedule.getServices().get(0).setServer(schedule.getServers().get(0));
        scoreVerifier.assertSoftWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount - 1).forEach(value -> {
            int dist = massCount - 1 - value;
            schedule.getServices().get(dist).setServer(schedule.getServers().get(0));
            scoreVerifier.assertSoftWeight(constraintName, - 14 / dist, schedule);
            schedule.getServices().get(dist).setServer(null);
        });
    }

    @Test
    void tooMuchExp() {
        final String constraintName = "tooMuchExp";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServers().get(0).setYear(Year.now().getValue() - 16);
        config.getServiceTypes().add(new ServiceType());
        config.getServiceTypes().get(0).setMinYear(Year.now().getValue() - 7);

        PlanningMassTemplate planningMassTemplate = new PlanningMassTemplate();
        planningMassTemplate.setServiceTypeCounts(Map.of(config.getServiceTypes().get(0), 1));

        Schedule schedule = new Schedule(config, List.of(planningMassTemplate));

        scoreVerifier.assertSoftWeight(constraintName, 0, schedule);
        schedule.getServices().get(0).setServer(schedule.getServers().get(0));
        scoreVerifier.assertSoftWeight(constraintName, - 5 * 9, schedule);
    }

    @Test
    void minimizeServices() {
        final String constraintName = "minimizeServices";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServers().add(new Server());
        config.getServers().get(0).setSurname("First");
        config.getServers().get(1).setSurname("Second");
        config.getServiceTypes().add(new ServiceType());

        List<PlanningMassTemplate> planningMassTemplates = generatePlanningMassTemplates(config, true, false);

        Schedule schedule = new Schedule(config, planningMassTemplates);

        scoreVerifier.assertSoftWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            int assignments = value + 1;
            int expectedWeight = - assignments * assignments;
            scoreVerifier.assertSoftWeight(constraintName, expectedWeight, schedule);
        });

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(1));
            int assignments = value + 1;
            int other = massCount - 1 - value;
            scoreVerifier.assertSoftWeight(constraintName, - other * other - assignments * assignments, schedule);
        });
    }

}
