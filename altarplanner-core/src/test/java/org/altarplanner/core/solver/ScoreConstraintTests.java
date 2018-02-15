package org.altarplanner.core.solver;

import org.altarplanner.core.domain.*;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ScoreConstraintTests {

    private HardSoftScoreVerifier<Schedule> scoreVerifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource("org/altarplanner/core/solver/solverConfig.xml"));

    private final static int massCount = 10;

    private List<DiscreteMass> generateDiscreteMasses(Config config, boolean subsequentMasses, boolean subsequentServiceTypes) {
        return IntStream.range(0, massCount)
                .mapToObj(value -> {
                    DiscreteMass discreteMass = new DiscreteMass();
                    discreteMass.setDate(subsequentMasses ? LocalDate.now().plusDays(value) : LocalDate.now());
                    discreteMass.getServiceTypeCount().put(config.getServiceTypes().get(subsequentServiceTypes ? value : 0), 1);
                    return discreteMass;
                })
                .collect(Collectors.toList());
    }

    @Test
    void oneServicePerDay() {
        final String constraintName = "oneServicePerDay";

        Config config = new Config();
        config.getServers().add(new Server());
        config.getServiceTypes().add(new ServiceType());

        List<DiscreteMass> discreteMasses = generateDiscreteMasses(config, false, false);

        Schedule schedule = new Schedule(null, discreteMasses, config);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            int expectedWeight = -value * (value + 1) / 2; // equal to -IntStream.range(0, value + 1).sum()
            scoreVerifier.assertHardWeight(constraintName, expectedWeight, schedule);
        });
    }

    @Test
    void notEnoughExperience() {
        final String constraintName = "notEnoughExperience";

        Config config = new Config();

        config.setServers(
                IntStream.range(0, massCount)
                        .mapToObj(value -> {
                            Server server = new Server();
                            server.setYear(LocalDate.now().getYear() - value);
                            return server;
                        })
                        .collect(Collectors.toList())
        );

        config.setServiceTypes(
                IntStream.range(0, massCount)
                        .mapToObj(value -> {
                            ServiceType serviceType = new ServiceType();
                            serviceType.setMinExp(value);
                            return serviceType;
                        })
                        .collect(Collectors.toList())
        );

        List<DiscreteMass> discreteMasses = generateDiscreteMasses(config, true, true);

        Schedule schedule = new Schedule(null, discreteMasses, config);

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
        config.getServers().get(0).getAbsences().add(new DateSpan(LocalDate.now(), LocalDate.now().plusDays(massCount / 2)));
        config.getServiceTypes().add(new ServiceType());

        List<DiscreteMass> discreteMasses = generateDiscreteMasses(config, true, false);

        Schedule schedule = new Schedule(null, discreteMasses, config);

        scoreVerifier.assertHardWeight(constraintName, 0, schedule);

        IntStream.range(0, massCount).forEach(value -> {
            schedule.getServices().get(value).setServer(schedule.getServers().get(0));
            scoreVerifier.assertHardWeight(constraintName, value <= massCount / 2 ? -1 : 0, schedule);
            schedule.getServices().get(value).setServer(null);
        });
    }

}
