package org.altarplanner.core.domain.util;

import org.altarplanner.core.domain.*;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.domain.DiscreteMassCollection;
import org.optaplanner.core.api.solver.SolverFactory;
import org.threeten.extra.LocalDateRange;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BigDomainGenerator {

    private static final LocalDate TODAY = LocalDate.of(2018, 1, 1);
    private static final LocalDateRange PLANNING_WINDOW;
    static {
        LocalDate nextMonday = TODAY.plusDays(DayOfWeek.MONDAY.getValue() - TODAY.getDayOfWeek().getValue());
        PLANNING_WINDOW = LocalDateRange.ofClosed(nextMonday, nextMonday.plusWeeks(5).minusDays(1));
    }

    private static LocalDate getNextDayOfWeek(LocalDate starting, DayOfWeek dayOfWeek) {
        return starting.plusDays(dayOfWeek.getValue() - starting.getDayOfWeek().getValue());
    }

    private static Server genServer(Random random) {
        Server server = new Server();
        server.setSurname("");
        server.setForename("");
        server.setYear(TODAY.getYear() - random.nextInt(10));

        if (random.nextFloat() < 0.4)
            server.getWeeklyAbsences().add(DayOfWeek.MONDAY);
        if (random.nextFloat() < 0.3)
            server.getWeeklyAbsences().add(DayOfWeek.TUESDAY);
        if (random.nextFloat() < 0.4)
            server.getWeeklyAbsences().add(DayOfWeek.WEDNESDAY);
        if (random.nextFloat() < 0.45)
            server.getWeeklyAbsences().add(DayOfWeek.THURSDAY);
        if (random.nextFloat() < 0.35)
            server.getWeeklyAbsences().add(DayOfWeek.FRIDAY);
        if (random.nextFloat() < 0.3)
            server.getWeeklyAbsences().add(DayOfWeek.SATURDAY);
        if (random.nextFloat() < 0.1)
            server.getWeeklyAbsences().add(DayOfWeek.SUNDAY);

        if (random.nextFloat() < 0.2)
            server.getAbsences().add(LocalDateRange.of(PLANNING_WINDOW.getStart().plusWeeks(1), PLANNING_WINDOW.getStart().plusWeeks(3)));

        if (random.nextFloat() < 0.05)
            server.getDateTimeOnWishes().add(LocalDateTime.of(getNextDayOfWeek(PLANNING_WINDOW.getStart(), DayOfWeek.SUNDAY).plusWeeks(random.nextInt(4)), LocalTime.of(11,0)));

        server.setSurname(Integer.toHexString(random.nextInt()));
        server.setForename(Integer.toHexString(random.nextInt()));

        return server;
    }

    public static Config genConfig() {
        final Random random = new Random(0);
        Config config = new Config();

        ServiceType altar1 = Builder.buildServiceType("Altar", TODAY.getYear() - 6,  TODAY.getYear() - 18);
        ServiceType altar2 = Builder.buildServiceType("Altar", TODAY.getYear() - 4, TODAY.getYear() - 6);
        ServiceType altar3 = Builder.buildServiceType("Altar", TODAY.getYear() - 2, TODAY.getYear() - 4);
        ServiceType cross = Builder.buildServiceType("Cross", TODAY.getYear() - 6, TODAY.getYear() - 18);
        ServiceType flambeau = Builder.buildServiceType("Flambeau", TODAY.getYear(), TODAY.getYear() - 2);
        ServiceType incense = Builder.buildServiceType("Incense", TODAY.getYear() - 6, TODAY.getYear() - 18);
        ServiceType lector = Builder.buildServiceType("Lector", TODAY.getYear() - 6, TODAY.getYear() - 18);
        config.setServiceTypes(List.of(altar3, altar2, altar1, cross, flambeau, incense, lector));

        RegularMass mon19 = Builder.buildRegularMass(DayOfWeek.MONDAY, LocalTime.of(19, 0), "Chapel", "Holy Mass",
                Map.of(altar3, 2, altar2, 2));
        RegularMass thu18 = Builder.buildRegularMass(DayOfWeek.THURSDAY, LocalTime.of(18, 0), "Chapel", "Holy Mass",
                Map.of(altar3, 2, altar2, 2));
        RegularMass sat17 = Builder.buildRegularMass(DayOfWeek.SATURDAY, LocalTime.of(17, 0), "Church", "Saturday Evening Mass",
                Map.of(altar3, 2, altar2, 2, altar1, 2));
        RegularMass sun08 = Builder.buildRegularMass(DayOfWeek.SUNDAY, LocalTime.of(8, 0), "Church", "Holy Mass",
                Map.of(altar3, 2, altar2, 2));
        RegularMass sun09 = Builder.buildRegularMass(DayOfWeek.SUNDAY, LocalTime.of(9, 45), "Chapel", "Holy Mass",
                Map.of(altar3, 2, altar2, 2));
        RegularMass sun11 = Builder.buildRegularMass(DayOfWeek.SUNDAY, LocalTime.of(11, 0), "Church", "High Mass",
                Map.of(altar2, 2, altar1, 2, flambeau, 10, lector, 1, incense, 2, cross, 1));
        config.setRegularMasses(List.of(mon19, thu18, sat17, sun08, sun09, sun11));

        List<Server> servers = IntStream.range(0, 100).mapToObj(value -> genServer(random)).collect(Collectors.toUnmodifiableList());

        servers.forEach(server -> {
            if (random.nextFloat() < .4)
                server.getInabilities().add(config.getServiceTypes().get(random.nextInt(7)));

            if (random.nextFloat() < .2)
                config.addPair(new PairRequest(server, servers.get(random.nextInt(servers.size()))));
        });

        config.setServers(servers);

        return config;
    }

    public static DiscreteMassCollection genDiscreteMassCollection() {
        List<DiscreteMass> masses = genConfig().getDiscreteMassStreamFromRegularMassesIn(PLANNING_WINDOW).collect(Collectors.toUnmodifiableList());
        return new DiscreteMassCollection(masses);
    }

    public static Schedule genSchedule() {
        Config config = genConfig();
        List<DiscreteMass> masses = config.getDiscreteMassStreamFromRegularMassesIn(PLANNING_WINDOW).collect(Collectors.toUnmodifiableList());
        return new Schedule(config, masses);
    }

    public static Schedule genInitializedSchedule() {
        Schedule uninitialized = genSchedule();
        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource("org/altarplanner/core/solver/reproducibleConstructionSolverConfig.xml");
        return solverFactory.buildSolver().solve(uninitialized);
    }

}
